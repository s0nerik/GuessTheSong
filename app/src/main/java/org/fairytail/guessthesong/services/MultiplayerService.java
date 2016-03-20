package org.fairytail.guessthesong.services;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.bluelinelabs.logansquare.LoganSquare;
import com.f2prateek.rx.receivers.wifi.RxWifiManager;
import com.github.davidmoten.rx.Bytes;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutServiceData;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.helpers.MpGameConverter;
import org.fairytail.guessthesong.helpers.SocketMessageFactory;
import org.fairytail.guessthesong.lib.ReactiveList;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.model.game.MpGame;
import org.fairytail.guessthesong.networking.entities.PlayerInfo;
import org.fairytail.guessthesong.networking.entities.SocketMessage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import in.workarounds.bundler.Bundler;
import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import lombok.val;
import ru.noties.debug.Debug;
import rx.Observable;
import rx.subjects.AsyncSubject;
import rx.subjects.Subject;

@RequireBundler
public class MultiplayerService extends Service {

    public class NetworkServiceStartException extends Exception {
        public NetworkServiceStartException() {
            super("Network service can't be started");
        }

        public NetworkServiceStartException(String detailMessage) {
            super(detailMessage);
        }

        public NetworkServiceStartException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public NetworkServiceStartException(Throwable throwable) {
            super(throwable);
        }
    }

    @Inject
    WifiManager wifiManager;

    @Arg
    HashMap<String, String> record;

    private Salut network;
    private IBinder binder = new MultiplayerServiceBinder();

    private MpGame currentGame;

    private SocketMessageFactory msgFactory = new SocketMessageFactory(UUID.randomUUID().toString());

    private ReactiveList<PlayerInfo> players = new ReactiveList<>();
    private ReactiveList<PlayerInfo> preparedPlayers = new ReactiveList<>();

    private Subject<SocketMessage, SocketMessage> messageSubject = AsyncSubject.create();

    private Observable<SocketMessage> requests =
            messageSubject.filter(msg -> msg.type == SocketMessage.Type.REQUEST);

    private Observable<SocketMessage> responses =
            messageSubject.filter(msg -> msg.type == SocketMessage.Type.RESPONSE);

    private Observable<SocketMessage> offers =
            messageSubject.filter(msg -> msg.type == SocketMessage.Type.OFFER);

    private SalutDeviceCallback deviceRegisteredReaction = device -> {
        Debug.d(device.readableName + " has connected!");
        PlayerInfo info = new PlayerInfo();
        info.id = device.txtRecord.get("id");
        info.setDevice(device);
        players.add(info);
    };

    public MultiplayerService() {
        super();
        Injector.inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundler.inject(this, intent);

        SalutDataReceiver dataReceiver =
                new SalutDataReceiver(App.getCurrentActivity(),
                                      o -> {
                                          try {
                                              SocketMessage msg = LoganSquare.parse((String) o, SocketMessage.class);
                                              messageSubject.onNext(msg);
                                          } catch (IOException e) {
                                              e.printStackTrace();
                                          }
                                      });
        SalutServiceData serviceData = new SalutServiceData("lwm", 50489, Build.MODEL);

        network = new Salut(dataReceiver, serviceData, () -> Debug.e("Sorry, but this device does not support WiFi Direct."));
        network.thisDevice.txtRecord.putAll(record);

        registerListeners();

        players.onItemAdded()
               .subscribe(event -> {
                   Debug.d(event.getItem().getDevice().readableName + " shared info about him!");
                   network.sendToDevice(event.getItem().getDevice(),
                                        msgFactory.newPrepareRequest(currentGame),
                                        () -> Debug.e("Can't prepare client"));
               });

        preparedPlayers.onItemAdded()
               .subscribe(event -> Debug.d(event.getItem().getDevice().readableName + " has prepared!"));

        return START_NOT_STICKY;
    }

    private void registerListeners() {
        val offerMessages = messageSubject.filter(msg -> msg.type == SocketMessage.Type.OFFER);

        val prepareMesssages = offerMessages
                .filter(msg -> msg.message == SocketMessage.Message.PREPARE);

        val successfulPrepares = prepareMesssages
                .filter(msg -> msg.status == SocketMessage.Status.OK);

        successfulPrepares.subscribe(msg -> {
            // Assuming that msg.body is a player's id
            PlayerInfo player = IterableUtils.find(players, d -> StringUtils.equals(d.id, msg.body));
            if (player != null)
                preparedPlayers.add(player);
        });

        val songRequests = requests.filter(msg -> msg.message == SocketMessage.Message.SONG);

        songRequests
                .concatMap(msg -> {
                    try {
                        return Bytes.from(new BufferedInputStream(new FileInputStream(msg.body)))
                                    .map(bytes -> Pair.<byte[], SocketMessage>create(bytes, msg));
                    } catch (FileNotFoundException e) {
                        return Observable.error(e);
                    }
                })
                .subscribe(pair -> {
                    network.sendToDevice(IterableUtils.find(players, o -> o.id.equals(pair.second.userId)).getDevice(),
                                         new String(pair.first),
                                         () -> Debug.e("Can't send a song to the client"));
                });
    }

    private Observable<Void> startNetworkServiceIfNotAlreadyStarted() {
        return Observable.<Void>create(subscriber -> {
            if (!network.isRunningAsHost) {
                network.startNetworkService(deviceRegisteredReaction,
                                            () -> {
                                                subscriber.onNext(null);
                                                subscriber.onCompleted();
                                            },
                                            () -> subscriber.onError(new NetworkServiceStartException()));
            } else {
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
    }

    private Observable<Void> enableWiFiIfNecessary() {
        return Observable.defer(() -> {
            if (!wifiManager.isWifiEnabled()) {
                return RxWifiManager.wifiStateChanges(getApplicationContext())
                                    .filter(state -> state == WifiManager.WIFI_STATE_ENABLED)
                                    .take(1)
                                    .map(s -> (Void) null)
                                    .delay(1, TimeUnit.SECONDS)
                                    .timeout(5, TimeUnit.SECONDS)
                                    .doOnSubscribe(() -> wifiManager.setWifiEnabled(true));
            }
            return Observable.just(null);
        });
    }

    public Observable<MpGame> prepareNewGame(Game game) {
        return new MpGameConverter(this).convertToMpGame(game)
                                        .concatMap(mpGame -> enableWiFiIfNecessary().map(arg -> mpGame))
                                        .concatMap(mpGame -> startNetworkServiceIfNotAlreadyStarted().map(arg -> mpGame))
                                        .doOnNext(mpGame1 -> currentGame = mpGame1);
    }

    @Override
    public void onDestroy() {
        network.stopNetworkService(false);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MultiplayerServiceBinder extends Binder {
        public MultiplayerService getService() {
            return MultiplayerService.this;
        }
    }
}
