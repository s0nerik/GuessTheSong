package org.fairytail.guessthesong.model.game;

import android.net.Uri;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.model.Song;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@JsonObject
@NoArgsConstructor
@EqualsAndHashCode(of = { "correctSong" })
public class Quiz implements Serializable {

    @JsonField
    Song correctSong;
    @JsonField
    ArrayList<Song> variants;
    @JsonField
    Difficulty difficulty;

    private long startTime;
    private boolean correct = false;

    // TODO: Handle a case when the song is shorter than it's required
    public Quiz(Song correctSong, List<Song> variants, Difficulty difficulty) {
        Injector.inject(this);
        this.correctSong = correctSong;
        this.variants = (ArrayList<Song>) variants;
        this.difficulty = difficulty;
        startTime = (long) (new Random().nextFloat()*(correctSong.getDuration() - difficulty.getSongDuration()));
    }

//    public void start() {
//        new Handler().postDelayed(() ->
//                App.bus.post(new QuizTimeOverEvent(this)), difficulty.getSongDuration());
//    }

    public boolean check(Song chosen) {
        correct = new SongsMatcher(correctSong, chosen).areSimilar();
        return correct;
    }

    public Uri getSongUri() {
        return Uri.fromFile(new File(correctSong.getSource()));
    }

    public Uri getRemoteSongUri() {
        return Uri.parse(getCorrectSong().getRemoteSource());
    }

}
