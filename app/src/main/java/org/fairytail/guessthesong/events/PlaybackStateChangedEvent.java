package org.fairytail.guessthesong.events;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaybackStateChangedEvent {
    public enum State { PREPARED, STARTED, PAUSED, STOPPED }
    public final State state;
}
