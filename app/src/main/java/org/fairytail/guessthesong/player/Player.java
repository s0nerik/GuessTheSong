package org.fairytail.guessthesong.player;

import org.fairytail.guessthesong.model.Song;

public interface Player {
    void play(Song song);
    void stop();
}
