package org.fairytail.guessthesong.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.fairytail.guessthesong.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity {

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
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    @OnClick(R.id.btn_single_player)
    public void onSinglePlayerClicked() {
        Intent intent = new Intent(this, DifficultyActivity.class);
        startActivity(intent);
    }

    /*@OnMessage
    public void onSongsAvailable(List<Song> songs) {
        Log.d(App.TAG, songs.toString());
    }*/

}
