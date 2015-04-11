package org.fairytail.guessthesong.model.game;

import org.fairytail.guessthesong.model.Song;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Quiz {

    private final Song song;

    private final int duration;

    private long startTime;
    private long endTime;

    public void start() {
        startTime = System.currentTimeMillis();
        endTime = startTime + duration;
    }

}
