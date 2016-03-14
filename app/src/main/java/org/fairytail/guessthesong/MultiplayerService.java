package org.fairytail.guessthesong;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutServiceData;

import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.helpers.MpGameConverter;
import org.fairytail.guessthesong.model.game.Game;

import java.util.Map;

import javax.inject.Inject;

import lombok.val;
import ru.noties.debug.Debug;

public class MultiplayerService extends Service {

    @Inject
    WifiManager wifiManager;

    private Salut network;

    public MultiplayerService() {
        super();
        Injector.inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        val record = (Map<String, String>) intent.getSerializableExtra("record");
        val game = (Game) intent.getSerializableExtra("game");

        SalutDataReceiver dataReceiver = new SalutDataReceiver(App.getCurrentActivity(), o -> Debug.d(o.toString()));
        SalutServiceData serviceData = new SalutServiceData("lwm", 50489, Build.MODEL);

        network = new Salut(dataReceiver, serviceData, () -> Debug.e("Sorry, but this device does not support WiFi Direct."));
        network.thisDevice.txtRecord.putAll(record);

        network.startNetworkService(device -> Debug.d(device.readableName + " has connected!"));

        final long[] startTime = new long[1];
        new MpGameConverter(this)
                .convertToMpGame(game)
                .doOnSubscribe(() -> { Debug.d("Game conversion started!"); startTime[0] = System.currentTimeMillis(); })
                .subscribe(mpGame -> Debug.d("GAME SUCCESSFULLY CONVERTED! "+(System.currentTimeMillis()-startTime[0])));

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        network.stopNetworkService(false);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
