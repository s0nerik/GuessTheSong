package org.fairytail.guessthesong.model.game;

import org.fairytail.guessthesong.model.Song;

import lombok.Builder;

@Builder
public class Quiz {

    private final Song song;

    private int start;
    private int end;

}
