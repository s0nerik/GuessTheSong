package org.fairytail.guessthesong.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.adapters.GameAdapter;
import org.fairytail.guessthesong.custom_views.NonSwipeableViewPager;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.events.QuizSongChosenEvent;
import org.fairytail.guessthesong.helpers.GamePlayer;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.model.game.Quiz;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.workarounds.bundler.Bundler;
import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import ru.noties.debug.Debug;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

@RequireBundler
public class GameActivity extends FragmentActivity {

    @Arg
    Game game;

    @Inject
    Bus bus;

    @InjectView(R.id.pager)
    NonSwipeableViewPager pager;

    GameAdapter gameAdapter;

    private GamePlayer player;

    private Subject<Quiz, Quiz> quizFinishSubject = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.inject(this);
        Injector.inject(this);
        Bundler.inject(this);
        bus.register(this);

        player = new GamePlayer(game);

        gameAdapter = new GameAdapter(getSupportFragmentManager(), game);

        player.prepare(true)
              .doOnNext(game1 -> {
                  Debug.d("player.prepare: onNext");
                  pager.setAdapter(gameAdapter);
              })
              .concatMap(this::play)
              .subscribe(g -> Debug.d("Game finished: "+g));
    }

    private Observable<Void> play(Game game) {
        Observable<Void> observable = Observable.<Void>empty();
        for (Quiz q : game.getQuizzes()) {
            Observable<Void> quizPlaybackObservable = Observable.timer(q.getDifficulty().getSongDuration(), TimeUnit.MILLISECONDS)
                                                                .doOnSubscribe(() -> player.start(q).subscribe())
                                                                .map(o -> null);

            Observable<Void> quizStopObservable = quizFinishSubject.filter(quiz -> quiz.equals(q))
                                                                   .delay(1, TimeUnit.SECONDS)
                                                                   .map(o -> null);

            observable = observable.concatWith(
                    quizPlaybackObservable.takeUntil(quizStopObservable)
                                          .doOnNext(quiz -> Debug.d("doOnNext: " + quiz))
                                          .doOnUnsubscribe(() -> {
                                              Debug.d("doOnUnsubscribe: " + q);
                                              player.stop(q).subscribe();
                                              goToNextPage();
                                          })
                                          .observeOn(AndroidSchedulers.mainThread())
            );
        }

        return observable.concatWith(Observable.just(null));
    }

//    @Subscribe
//    public void onQuizTimeOver(QuizTimeOverEvent event) {
////        player.stop();
//
//        if (!isMultiplayer && event.getQuiz().equals(game.getQuizzes().get(pager.getCurrentItem()))) {
//            goToNextPage();
//        }
//    }

    @Subscribe
    public void onQuizSongChosen(QuizSongChosenEvent event) {
        Debug.d("Quiz song chosen!");
//        goToNextPage();
        quizFinishSubject.onNext(event.getQuiz());
//        player.stop();

//        if (!isMultiplayer) {
//            goToNextPage();
//        }
    }

    private void goToNextPage() {
//        new Handler().postDelayed(() -> {
            if ((pager.getCurrentItem() + 1) == game.getQuizzes().size()) {
                int score = game.countCorrectQuizzes();
                Intent intent = new Intent(this, ScoreActivity.class);
                Bundle b = new Bundle();
                b.putInt("score", score);
                intent.putExtras(b);
                startActivity(intent);
                finish();
            } else {
                pager.setCurrentItem(pager.getCurrentItem() + 1);
            }
//        }, 1500);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        player.stop();
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

}