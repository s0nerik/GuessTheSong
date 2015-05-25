package org.fairytail.guessthesong.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.adapters.GameAdapter;
import org.fairytail.guessthesong.custom_views.NonSwipeableViewPager;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.events.QuizSongChosenEvent;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.model.game.Quiz;
import org.fairytail.guessthesong.player.Player;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.noties.debug.Debug;

public class GameActivity extends FragmentActivity {

    static final String TAG = "myLogs";

    GameAdapter gAdapter;

    @Inject
    Bus bus;

    @Inject
    Player player;

    @InjectView(R.id.pager)
    NonSwipeableViewPager pager;

    private Game game;
    public boolean isMultiplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.inject(this);
        Injector.inject(this);
        bus.register(this);

        Bundle extras = getIntent().getExtras();

        game = (Game) extras.getSerializable("game");
        isMultiplayer = extras.getBoolean("multiplayer", false);

        if (isMultiplayer) {
            Debug.d("isMultiplayer");
        }

        gAdapter = new GameAdapter(getSupportFragmentManager(), game);
        pager.setAdapter(gAdapter);

        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                pageSelectedListener(position);
            }
        });

        if (pager.getCurrentItem() == 0) {
            pageSelectedListener(0);
        }
    }

    private void pageSelectedListener(int position) {
        if (!isMultiplayer) {
            Quiz thisQuiz = game.getQuizzes().get(position);
            player.prepareAndSeekTo(thisQuiz.getCorrectSong(), 40 * 1000, Player::start);
            thisQuiz.start();
        }
    }

    @Subscribe
    public void onQuizSongChoosen(QuizSongChosenEvent event) {
        new Handler().postDelayed(() -> pager.setCurrentItem(pager.getCurrentItem() + 1), 1500);
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.stop();
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

}