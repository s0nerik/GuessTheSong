package org.fairytail.guessthesong.model.game;

import android.net.Uri;
import android.os.Handler;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.events.QuizTimeOverEvent;
import org.fairytail.guessthesong.model.Song;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonObject
@NoArgsConstructor
public class Quiz implements Serializable {

    @JsonField
    Song correctSong;
    @JsonField
    ArrayList<Song> variants;
    @JsonField
    Difficulty difficulty;

    private long startTime;
    private long endTime;
    private boolean correct = false;

    public Quiz(Song correctSong, List<Song> variants, Difficulty difficulty) {
        Injector.inject(this);
        this.correctSong = correctSong;
        this.variants = (ArrayList<Song>) variants;
        this.difficulty = difficulty;
        startTime = (long) (new Random().nextFloat()*correctSong.getDuration());
        endTime = startTime + difficulty.getSongDuration();
    }

    public void start() {
        new Handler().postDelayed(() ->
                App.bus.post(new QuizTimeOverEvent(this)), difficulty.getSongDuration());
    }

    public boolean check(Song chosen) {
        correct = new SongsMatcher(correctSong, chosen).areSimilar();
        return correct;
    }

    public Uri getSongUri() {
        return Uri.fromFile(new File(correctSong.getSource()));
    }

}
