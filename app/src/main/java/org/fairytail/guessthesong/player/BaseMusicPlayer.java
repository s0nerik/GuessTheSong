package org.fairytail.guessthesong.player;

import android.media.AudioManager;
import android.media.MediaPlayer;

import org.fairytail.guessthesong.model.Song;

import java.io.IOException;

public abstract class BaseMusicPlayer implements Player {

    private MediaPlayer player;

    private void resetPlayer() {
        if (player != null) {
            player.release();
        }
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(getOnPreparedListener());
    }

    protected abstract void handleDataSourceError(Song song);
    protected abstract MediaPlayer.OnPreparedListener getOnPreparedListener();

    @Override
    public void play(Song song) {
        resetPlayer();
        try {
            player.setDataSource(song.getSource());
            player.prepareAsync();
        } catch (IOException e) {
            handleDataSourceError(song);
        }
    }

    @Override
    public void stop() {
        player.stop();
    }
}
