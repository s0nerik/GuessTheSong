package org.fairytail.guessthesong.events.ui;

import com.peak.salut.SalutDevice;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MpGameSelectedEvent {
    public final SalutDevice device;
}
