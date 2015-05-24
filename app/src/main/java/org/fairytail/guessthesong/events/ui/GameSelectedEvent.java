package org.fairytail.guessthesong.events.ui;

import android.net.wifi.p2p.WifiP2pDevice;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameSelectedEvent {
    public final WifiP2pDevice device;
}
