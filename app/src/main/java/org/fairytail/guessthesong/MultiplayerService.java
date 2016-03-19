package org.fairytail.guessthesong;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.LoganSquare;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutServiceData;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.helpers.MpGameConverter;
import org.fairytail.guessthesong.lib.ReactiveList;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.model.game.MpGame;
import org.fairytail.guessthesong.networking.entities.PlayerInfo;
import org.fairytail.guessthesong.networking.entities.SocketMessage;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

import lombok.val;
import ru.noties.debug.Debug;
import rx.Observable;
import rx.subjects.AsyncSubject;
import rx.subjects.Subject;

public class MultiplayerService extends Service {

    @Inject
    WifiManager wifiManager;

    private Salut network;
    private IBinder binder = new MultiplayerBinder();

    private MpGame currentGame;

    private ReactiveList<PlayerInfo> players = new ReactiveList<>();
    private ReactiveList<PlayerInfo> preparedPlayers = new ReactiveList<>();

    private Subject<SocketMessage, SocketMessage> messageSubject = AsyncSubject.create();

    public MultiplayerService() {
        super();
        Injector.inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        val record = (Map<String, String>) intent.getSerializableExtra("record");
        val game = (Game) intent.getSerializableExtra("game");

        SalutDataReceiver dataReceiver =
                new SalutDataReceiver(App.getCurrentActivity(),
                                      o -> {
                                          try {
                                              messageSubject.onNext(LoganSquare.parse((String) o, SocketMessage.class));
                                          } catch (IOException e) {
                                              e.printStackTrace();
                                          }
                                      });
        SalutServiceData serviceData = new SalutServiceData("lwm", 50489, Build.MODEL);

        network = new Salut(dataReceiver, serviceData, () -> Debug.e("Sorry, but this device does not support WiFi Direct."));
        network.thisDevice.txtRecord.putAll(record);

        network.startNetworkService(device -> {
            Debug.d(device.readableName + " has connected!");
            PlayerInfo info = new PlayerInfo();
            info.id = device.txtRecord.get("id");
            info.setDevice(device);
            players.add(info);
        });

        players.onItemAdded()
               .subscribe(event -> {
                   Debug.d(event.getItem().getDevice().readableName + " shared info about him!");
                   network.sendToDevice(event.getItem().getDevice(),
                                        SocketMessage.newPrepareMessage(currentGame),
                                        () -> Debug.e("Can't prepare client"));
               });

        preparedPlayers.onItemAdded()
               .subscribe(event -> Debug.d(event.getItem().getDevice().readableName + " has prepared!"));

        if (game != null)
            prepareNewGame(game).subscribe(mpGame -> currentGame = mpGame);

        return START_NOT_STICKY;
    }

    private void registerListeners() {
        val postMessages = messageSubject.filter(msg -> msg.type == SocketMessage.Type.OFFER);

        val prepareMesssages = postMessages
                .filter(msg -> msg.message == SocketMessage.Message.PREPARE);

        val successfulPrepares = prepareMesssages
                .filter(msg -> msg.status == SocketMessage.Status.OK);

//        val playerInfoMesssages = postMessages
//                .filter(msg -> msg.message == SocketMessage.Message.PLAYER_INFO);

//        playerInfoMesssages.subscribe(socketMessage -> {
//            try {
//                PlayerInfo info = LoganSquare.parse(socketMessage.body, PlayerInfo.class);
//                players.add(info);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });

        successfulPrepares.subscribe(msg -> {
            // Assuming that msg.body is a player's id
            PlayerInfo player = IterableUtils.find(players, d -> StringUtils.equals(d.id, msg.body));
            if (player != null)
                preparedPlayers.add(player);
        });
    }

    public Observable<MpGame> prepareNewGame(Game game) {
        return new MpGameConverter(this).convertToMpGame(game);
    }

//    public Observable<MpGame> startGame(MpGame game) {
//        return preparePlayers(game);
//    }

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

    public class MultiplayerBinder extends Binder {
        public MultiplayerService getService() {
            return MultiplayerService.this;
        }
    }
}
