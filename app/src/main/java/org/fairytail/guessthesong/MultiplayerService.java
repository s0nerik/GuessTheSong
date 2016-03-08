package org.fairytail.guessthesong;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.networking.p2p.RxWifiP2pManager;

import java.util.Map;

import javax.inject.Inject;

import lombok.val;
import ru.noties.debug.Debug;

public class MultiplayerService extends Service {

    @Inject
    WifiP2pManager manager;

    private WifiP2pManager.Channel channel;

    public MultiplayerService() {
        super();
        Injector.inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        val record = (Map<String, String>) intent.getSerializableExtra("record");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("LWM", "_lwm._tcp", record);

        channel = manager.initialize(this, getMainLooper(), () -> Debug.d("Channel disconnected!"));
        RxWifiP2pManager.addLocalService(manager, channel, serviceInfo)
                .subscribe(aVoid -> Debug.d("addLocalService: success"));

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        manager.clearLocalServices(channel, null);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
