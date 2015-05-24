package org.fairytail.guessthesong.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.adapters.GameAdapter;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.networking.ws.GameWebSocketClient;

import butterknife.ButterKnife;
import ru.noties.debug.Debug;

public class GameActivity extends FragmentActivity {

    static final String TAG = "myLogs";

    GameAdapter gAdapter;

    private GameWebSocketClient gameWebSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.inject(this);
        Injector.inject(this);

        Bundle extras = getIntent().getExtras();

        Game game = (Game) extras.getSerializable("game");

        boolean isMultiplayer = extras.getBoolean("multiplayer", false);

        if (isMultiplayer) {
            Debug.d("isMultiplayer");
        }

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        gAdapter = new GameAdapter(getSupportFragmentManager());
        pager.setAdapter(gAdapter);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected, position = " + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

}