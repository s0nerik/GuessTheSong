package org.fairytail.guessthesong.player;

import android.media.MediaPlayer;
import android.util.Log;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.model.Song;

public class MusicPlayer extends BaseMusicPlayer implements MediaPlayer.OnPreparedListener {

    @Override
    protected void handleDataSourceError(Song song) {
        Log.d(App.TAG, "handleDataSourceError, song: " + song);
    }

    @Override
    protected MediaPlayer.OnPreparedListener getOnPreparedListener() {
        return this;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}
