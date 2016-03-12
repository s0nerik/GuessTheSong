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

import java.util.Map;

import javax.inject.Inject;

import lombok.val;
import ru.noties.debug.Debug;

public class MultiplayerService extends Service {

    private Salut network;

    @Inject
    WifiManager wifiManager;

    public MultiplayerService() {
        super();
        Injector.inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        val record = (Map<String, String>) intent.getSerializableExtra("record");

        SalutDataReceiver dataReceiver = new SalutDataReceiver(App.getCurrentActivity(), o -> Debug.d(o.toString()));
        SalutServiceData serviceData = new SalutServiceData("lwm", 50489, Build.MODEL);

        network = new Salut(dataReceiver, serviceData, () -> Debug.e("Sorry, but this device does not support WiFi Direct."));
        network.thisDevice.txtRecord.putAll(record);

        network.startNetworkService(device -> Debug.d(device.readableName + " has connected!"));
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
