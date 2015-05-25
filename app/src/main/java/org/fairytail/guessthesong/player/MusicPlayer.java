package org.fairytail.guessthesong.player;

import android.media.MediaPlayer;
import android.util.Log;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.model.Song;

import ru.noties.debug.Debug;

public class MusicPlayer extends BaseMusicPlayer {

    @Override
    protected void handleDataSourceError(Song song) {
        Log.d(App.TAG, "handleDataSourceError, song: " + song);
    }

    @Override
    protected MediaPlayer.OnPreparedListener getOnPreparedListener() {
        return mp -> Debug.d("OnPreparedListener");
//        return MediaPlayer::start;
    }

    @Override
    protected MediaPlayer.OnSeekCompleteListener getOnSeekCompleteListener() {
        return mp -> Debug.d("OnSeekCompleteListener");
    }
}
