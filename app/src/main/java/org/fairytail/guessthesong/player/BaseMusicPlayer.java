package org.fairytail.guessthesong.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.events.PlaybackStateChangedEvent;
import org.fairytail.guessthesong.model.Song;

import java.io.IOException;

import lombok.Getter;
import lombok.experimental.Delegate;

public abstract class BaseMusicPlayer implements Player {

    @Getter
    private Song currentSong;

    private interface PlayerDelegate {
//        void start();
        void pause();
        void stop();
    }

    @Delegate(types = PlayerDelegate.class)
    private MediaPlayer player;

    private void resetPlayer() {
        if (player != null) {
            player.release();
        }
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    protected abstract void handleDataSourceError(Song song);
    protected abstract MediaPlayer.OnPreparedListener getOnPreparedListener();
    protected abstract MediaPlayer.OnSeekCompleteListener getOnSeekCompleteListener();

    private void prepareWithListener(Song song, MediaPlayer.OnPreparedListener listener) {
        currentSong = song;
        resetPlayer();
        player.setOnPreparedListener(mp -> {
            listener.onPrepared(mp);
            player.setOnPreparedListener(getOnPreparedListener());
        });
        try {
            player.setDataSource(song.getSource());
            player.prepareAsync();
        } catch (IOException e) {
            handleDataSourceError(song);
        }
    }

    @Override
    public void prepare(Song song, @Nullable ActionCompletedListener listener) {
        prepareWithListener(song, mp -> {
            getOnPreparedListener().onPrepared(mp);
            if (listener != null) listener.onActionCompleted(this);
        });
    }

    @Override
    public void prepareAndSeekTo(Song song, int msec, @Nullable ActionCompletedListener listener) {
        prepareWithListener(song, mp -> {
            mp.setOnSeekCompleteListener(mediaPlayer -> {
                getOnPreparedListener().onPrepared(mp);
                getOnSeekCompleteListener().onSeekComplete(mp);
                if (listener != null) listener.onActionCompleted(BaseMusicPlayer.this);
                mp.setOnSeekCompleteListener(getOnSeekCompleteListener());
            });
            mp.seekTo(msec);
        });
    }

    public void start() {
        player.start();
        App.bus.post(new PlaybackStateChangedEvent(PlaybackStateChangedEvent.State.STARTED));
    }

}
