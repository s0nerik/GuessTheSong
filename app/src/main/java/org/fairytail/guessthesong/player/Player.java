package org.fairytail.guessthesong.player;

import org.fairytail.guessthesong.model.Song;

public interface Player {
    interface ActionCompletedListener {
        void onActionCompleted(Player player);
    }

    void prepare(Song song, ActionCompletedListener listener);
    void prepareAndSeekTo(Song song, int msec, ActionCompletedListener listener);
    void start();
    void pause();
    void stop();
}
