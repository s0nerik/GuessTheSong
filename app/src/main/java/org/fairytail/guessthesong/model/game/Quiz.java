package org.fairytail.guessthesong.model.game;

import org.fairytail.guessthesong.model.Song;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Quiz {

    private final Song correctSong;
    private final List<Song> variants;

    private final Difficulty difficulty;

    private long startTime;
    private long endTime;

    public void start() {
        startTime = System.currentTimeMillis();
        endTime = startTime + difficulty.getSongDuration();
    }

}
