package org.fairytail.guessthesong.helpers;

import android.content.Context;
import android.net.Uri;

import com.github.sonerik.rxexoplayer.BasicRxExoPlayer;
import com.github.sonerik.rxexoplayer.RxExoPlayer;

import org.fairytail.guessthesong.dagger.Daggered;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.model.game.Quiz;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;
import lombok.val;
import rx.Observable;

@RequiredArgsConstructor
public class GamePlayer extends Daggered {
    @Inject
    Context context;

    private final Game game;

    private final Map<Quiz, RxExoPlayer> players = new HashMap<>();

    public Observable<Game> prepare() {
        val observable = Observable.just(game);
        for (Quiz q : game.getQuizzes()) {
            val player = new BasicRxExoPlayer(context);
            players.put(q, player);
            observable = observable.concatMap(g -> player.prepare(Uri.fromFile(new File(q.getCorrectSong().getSource()))).map(e -> g));
        }
        return observable;
    }

    public Observable start(Quiz q) {
        return players.get(q).start();
    }

    public Observable stop(Quiz q) {
        return players.get(q).stop();
    }
}
