package org.fairytail.guessthesong.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.eftimoff.androidplayer.actions.property.*;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.joanzapata.android.asyncservice.api.annotation.InjectService;
import com.joanzapata.android.asyncservice.api.annotation.OnMessage;
import com.joanzapata.android.asyncservice.api.internal.AsyncService;

import org.fairytail.guessthesong.App;
import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.async.SongsGetterService;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.db.Order;
import org.fairytail.guessthesong.player.Player;

import javax.inject.Inject;

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
    @InjectView(R.id.imageView)
    ImageView imgName;

    @Inject
    Player player;

    PropertyAction imgAction;
    PropertyAction btnSingleAction;
    PropertyAction btnMultiAction;
    float width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        AsyncService.inject(this);
        Injector.inject(this);

        imgName.setVisibility(View.INVISIBLE);
        width = getWindowManager().getDefaultDisplay().getWidth();

        imgAction = PropertyAction.newPropertyAction(img)
                .interpolator(new AccelerateDecelerateInterpolator())
                .scaleX(0)
                .scaleY(0)
                .rotation(-540)
                .duration(800)
                .alpha(0f)
                .build();

        btnSingleAction = PropertyAction.newPropertyAction(btnSinglePlayer)
                .interpolator(new DecelerateInterpolator())
                .translationY(500)
                .duration(700)
                .alpha(0f)
                .build();

        btnMultiAction = PropertyAction.newPropertyAction(btnMultiPlayer)
                .interpolator(new DecelerateInterpolator())
                .translationY(500)
                .delay(100)
                .duration(700)
                .alpha(0f)
                .build();

        com.eftimoff.androidplayer.Player.init()
                .animate(imgAction)
                .animate(btnSingleAction)
                .animate(btnMultiAction)
                .play();

        Handler handler = new Handler();

        handler.postDelayed(() -> {
            imgName.setVisibility(View.VISIBLE);

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
                    spring.setVelocity(0.01);

                    float value = (float) spring.getCurrentValue();
                    float rotate = value*150;
                    float translation = value * (width / 2) + 30;
                    float translation2 = value*(-(getResources().getDimension(R.dimen.name_left_margin)));

                    img.setRotation(rotate);
                    img.setTranslationX(translation);
                    imgName.setTranslationX(translation2);
                }
            });

            // Set the spring in motion; moving from 0 to 1
            spring.setEndValue(1);
        }, 1500);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        songsGetterService.loadAllSongs(Order.RANDOM);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.btn_single_player)
    public void onSinglePlayerClicked() {
        Intent intent = new Intent(this, DifficultyActivity.class);
        startActivity(intent);
    }

    @OnMessage
    public void onSongsAvailable(SongsGetterService.SongsListLoadedEvent e) {
        Log.d(App.TAG, e.getSongs().toString());
        //player.play(e.getSongs().get(0));
    }

}
