package org.fairytail.guessthesong.events.ui;

import android.net.wifi.p2p.WifiP2pDevice;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WifiP2pDeviceSelectedEvent {
    public final WifiP2pDevice device;
}
