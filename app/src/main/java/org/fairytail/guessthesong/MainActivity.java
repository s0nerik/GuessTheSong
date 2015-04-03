package org.fairytail.guessthesong;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();

        YoYo.with(Techniques.FadeIn)
                .duration(1500)
                .playOn(findViewById(R.id.imageView));

        YoYo.with(Techniques.SlideInRight)
                .duration(500)
                .playOn(findViewById(R.id.singlePlayerButton));

        YoYo.with(Techniques.SlideInRight)
                .delay(100)
                .duration(500)
                .playOn(findViewById(R.id.multiPlayerButton));
    }

    public void onSingleButtonClick(View view) {

        //Landing
        //BounceIn
        //SlideInRight

    }
}
