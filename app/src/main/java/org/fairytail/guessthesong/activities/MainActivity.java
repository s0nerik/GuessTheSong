package org.fairytail.guessthesong.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.joanzapata.android.asyncservice.api.annotation.InjectService;
import com.joanzapata.android.asyncservice.api.annotation.OnMessage;
import com.joanzapata.android.asyncservice.api.internal.AsyncService;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.db.Order;
import org.fairytail.guessthesong.db.SongsGetterService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity {

    @InjectService
    SongsGetterService songsGetterService;

    @InjectView(R.id.btn_single_player)
    Button btnSinglePlayer;
    @InjectView(R.id.btn_multi_player)
    Button btnMultiPlayer;
    @InjectView(R.id.img)
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        AsyncService.inject(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        songsGetterService.loadAllSongs(Order.RANDOM);
    }

    @Override
    protected void onResume() {
        super.onResume();

        img.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                img.setVisibility(View.VISIBLE);

                // Create a system to run the physics loop for a set of springs.
                SpringSystem springSystem = SpringSystem.create();

                // Add a spring to the system.
                Spring spring = springSystem.createSpring();

                // Add a listener to observe the motion of the spring.
                spring.addListener(new SimpleSpringListener() {

                    @Override
                    public void onSpringUpdate(Spring spring) {
                        // You can observe the updates in the spring
                        // state by asking its current value in onSpringUpdate.

                        float value = (float) spring.getCurrentValue();
                        //float scale = 1f + (value * 0.5f);
                        float scale = 0f + (value);
                        img.setScaleX(scale);
                        img.setScaleY(scale);
                    }
                });

                // Set the spring in motion; moving from 0 to 1
                spring.setEndValue(1);
            }
        }, 1000);

    }

    @OnClick(R.id.btn_single_player)
    public void onSinglePlayerClicked() {
        Intent intent = new Intent(this, DifficultyActivity.class);
        startActivity(intent);
    }

    @OnMessage
    public void onSongsAvailable(SongsGetterService.SongsListLoadedEvent e) {
        Log.d(App.TAG, e.getSongs().toString());
    }

}
