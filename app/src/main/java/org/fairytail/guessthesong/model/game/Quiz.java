package org.fairytail.guessthesong.model.game;

import android.os.Handler;

import com.squareup.otto.Bus;

import org.fairytail.guessthesong.dagger.Daggered;
import org.fairytail.guessthesong.events.QuizTimeOverEvent;
import org.fairytail.guessthesong.model.Song;

import java.util.List;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Quiz extends Daggered {

    @Inject
    Bus bus;

    private final Song correctSong;
    private final List<Song> variants;

    private final Difficulty difficulty;

    private long startTime;
    private long endTime;

    public void start() {
        startTime = System.currentTimeMillis();
        endTime = startTime + difficulty.getSongDuration();

        new Handler().postDelayed(() ->
                bus.post(new QuizTimeOverEvent(this)), difficulty.getSongDuration());
    }

}
