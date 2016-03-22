package org.fairytail.guessthesong.services;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.LoganSquare;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutServiceData;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.helpers.SocketMessageFactory;
import org.fairytail.guessthesong.helpers.service_binder.RxServiceBinder;
import org.fairytail.guessthesong.model.game.MpGame;
import org.fairytail.guessthesong.networking.entities.SocketMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.UUID;

import javax.inject.Inject;

import in.workarounds.bundler.Bundler;
import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import lombok.val;
import ru.noties.debug.Debug;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

@RequireBundler
public abstract class MultiplayerService extends Service {

    @Inject
    WifiManager wifiManager;

    @Arg
    HashMap<String, String> record;

    private IBinder binder = new Binder();

    protected Salut network;

    protected MpGame currentGame;

    protected SocketMessageFactory msgFactory = new SocketMessageFactory(UUID.randomUUID().toString());

    private Subscription subscription;

    private Subject<SocketMessage, SocketMessage> messageSubject = PublishSubject.create();

    protected Observable<SocketMessage> requests =
            messageSubject.filter(msg -> msg.type == SocketMessage.Type.REQUEST);

    protected Observable<SocketMessage> responses =
            messageSubject.filter(msg -> msg.type == SocketMessage.Type.RESPONSE);

    protected Observable<SocketMessage> offers =
            messageSubject.filter(msg -> msg.type == SocketMessage.Type.OFFER);

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
                                              Debug.e(e);
                                              e.printStackTrace();
                                          }
                                      });

        int port = 1337;
        try {
            val socket = new ServerSocket(0);
            port = socket.getLocalPort();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SalutServiceData serviceData = new SalutServiceData("lwm", port, Build.MODEL);

        network = new Salut(dataReceiver, serviceData, () -> Debug.e("Sorry, but this device does not support WiFi Direct."));
        network.thisDevice.txtRecord.putAll(record);

        subscription = new CompositeSubscription(subscribeListeners());

        return START_NOT_STICKY;
    }

    protected abstract Subscription[] subscribeListeners();

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
        network.stopNetworkService(false);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class Binder extends android.os.Binder implements RxServiceBinder {
        public MultiplayerService getService() {
            return MultiplayerService.this;
        }
    }
}
