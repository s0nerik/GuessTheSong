package org.fairytail.guessthesong.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;

import org.fairytail.guessthesong.dagger.Injector;

import javax.inject.Inject;

import ru.noties.debug.Debug;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    @Inject
    WifiP2pManager manager;

    private WifiP2pManager.Channel channel;

    public WiFiDirectBroadcastReceiver(WifiP2pManager.Channel channel) {
        super();
        Injector.inject(this);
        this.channel = channel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Debug.d(action);

        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION: break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION: break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION: break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION: break;
        }
    }
}