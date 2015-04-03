package org.fairytail.guessthesong.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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

        img.postDelayed(new Runnable() {
            @Override
            public void run() {

            YoYo.with(Techniques.FadeIn)
                    .duration(800)
                    .playOn(img);

            YoYo.with(Techniques.SlideInRight)
                    .duration(500)
                    .playOn(btnSinglePlayer);

            YoYo.with(Techniques.SlideInRight)
                    .delay(50)
                    .duration(500)
                    .playOn(btnMultiPlayer);
            }
        }, 500);

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
