package org.fairytail.guessthesong.model.game;

import android.os.Handler;

import com.squareup.otto.Bus;

import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.events.QuizTimeOverEvent;
import org.fairytail.guessthesong.model.Song;

import java.util.List;

import javax.inject.Inject;

import lombok.Data;

@Data
public class Quiz {

    @Inject
    Bus bus;

    private final Song correctSong;
    private final List<Song> variants;

    private final Difficulty difficulty;

    private long startTime;
    private long endTime;

    public Quiz(Song correctSong, List<Song> variants, Difficulty difficulty) {
        Injector.inject(this);
        this.correctSong = correctSong;
        this.variants = variants;
        this.difficulty = difficulty;
    }

    public void start() {
        startTime = System.currentTimeMillis();
        endTime = startTime + difficulty.getSongDuration();

        new Handler().postDelayed(() ->
                bus.post(new QuizTimeOverEvent(this)), difficulty.getSongDuration());
    }

    public boolean check(Song chosen) {
        return new SongsMatcher(correctSong, chosen).areSimilar();
    }

}
