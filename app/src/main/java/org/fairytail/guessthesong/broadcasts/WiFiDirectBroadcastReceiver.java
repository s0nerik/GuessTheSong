package org.fairytail.guessthesong.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.squareup.otto.Bus;

import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.events.p2p.P2PBroadcastReceivedEvent;

import javax.inject.Inject;

import ru.noties.debug.Debug;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    @Inject
    Bus bus;

    public WiFiDirectBroadcastReceiver() {
        super();
        Injector.inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Debug.d(action);
        bus.post(new P2PBroadcastReceivedEvent(action));
    }
}