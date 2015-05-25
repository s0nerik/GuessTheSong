package org.fairytail.guessthesong.networking.entities;

import org.fairytail.guessthesong.model.Song;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartFromEntity {

    public final int time;
    public final Song song;

}
