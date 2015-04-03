package org.fairytail.guessthesong.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.fairytail.guessthesong.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends FragmentActivity {

    @InjectView(R.id.singlePlayerButton)
    Button singlePlayerButton;
    @InjectView(R.id.multiPlayerButton)
    Button multiPlayerButton;
    @InjectView(R.id.imageView)
    ImageView imageView;

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
                .playOn(imageView);

        YoYo.with(Techniques.SlideInRight)
                .duration(500)
                .playOn(singlePlayerButton);

        YoYo.with(Techniques.SlideInRight)
                .delay(50)
                .duration(500)
                .playOn(multiPlayerButton);
    }
}
