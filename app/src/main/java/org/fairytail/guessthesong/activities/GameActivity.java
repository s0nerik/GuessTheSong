package org.fairytail.guessthesong.activities;

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

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.workarounds.bundler.Bundler;
import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.inject(this);
        Injector.inject(this);
        Bundler.inject(this);
        bus.register(this);

        gameAdapter = new GameAdapter(getSupportFragmentManager(), game);
        pager.setAdapter(gameAdapter);

//        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                pageSelectedListener(position);
//            }
//        });

//        if (pager.getCurrentItem() == 0) {
//            pageSelectedListener(0);
//        }
    }

//    private void pageSelectedListener(int position) {
//        if (!isMultiplayer) {
//            Quiz thisQuiz = game.getQuizzes().get(position);
//            player.prepareAndSeekTo(thisQuiz.getCorrectSong(), 40 * 1000, Player::start);
//            thisQuiz.start();
//        }
//    }

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
//        player.stop();

//        if (!isMultiplayer) {
//            goToNextPage();
//        }
    }

    private void goToNextPage() {
//        new Handler().postDelayed(() -> {
//            if ((pager.getCurrentItem() + 1) == game.getQuizzes().size()) {
//                int score = game.countCorrectQuizzes();
//                Intent intent = new Intent(this, ScoreActivity.class);
//                Bundle b = new Bundle();
//                b.putInt("score", score);
//                intent.putExtras(b);
//                startActivity(intent);
//                finish();
//            } else {
//                pager.setCurrentItem(pager.getCurrentItem() + 1);
//            }
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