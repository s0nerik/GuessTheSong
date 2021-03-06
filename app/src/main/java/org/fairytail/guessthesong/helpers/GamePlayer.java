package org.fairytail.guessthesong.helpers;

import android.content.Context;

import com.github.sonerik.rxexoplayer.BasicRxExoPlayer;
import com.github.sonerik.rxexoplayer.RxExoPlayer;

import org.fairytail.guessthesong.dagger.Daggered;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.model.game.Quiz;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import lombok.val;
import rx.Observable;

public class GamePlayer extends Daggered {
    @Inject
    Context context;

    private final Game game;

    public GamePlayer(Game game) {
        this.game = game;
    }

    private final Map<Quiz, RxExoPlayer> players = new HashMap<>();

    public Observable<Game> prepare() {
        return prepare(true);
    }

    public Observable<Game> prepare(boolean local) {
        Observable<Game> observable = Observable.just(game);
        for (Quiz q : game.getQuizzes()) {
            val player = new BasicRxExoPlayer(context);
            players.put(q, player);

            val prepareObservable = player.prepare(local ? q.getSongUri() : q.getRemoteSongUri())
                                          .concatMap(event -> player.seekTo(q.getStartTime()));

            observable = observable.concatMap(g -> prepareObservable.map(e -> g));
        }
        return observable;
    }

    public Observable<Quiz> start(Quiz q) {
        return players.get(q).start().map(event -> q);
    }

    public Observable<Quiz> stop(Quiz q) {
        return players.get(q).stop().map(event -> q);
    }

    public Observable<Void> stop() {
        Observable<Void> observable = Observable.<Void>empty();
        for (RxExoPlayer p : players.values()) {
            observable = observable.mergeWith(p.stop().map(event -> null));
        }
        return observable;
    }

    public void release() {
        for (RxExoPlayer p : players.values()) {
            p.release();
        }
    }
}
