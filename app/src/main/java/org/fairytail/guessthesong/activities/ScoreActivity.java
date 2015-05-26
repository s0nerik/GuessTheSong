package org.fairytail.guessthesong.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.sefford.circularprogressdrawable.CircularProgressDrawable;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.dagger.Injector;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ScoreActivity extends FragmentActivity{
    @InjectView(R.id.percentage)
    TextView percentage;
    @InjectView(R.id.textView)
    TextView title;
    @InjectView(R.id.circle_progress_bar)
    ProgressBar mProgress;

    int score;
    int percents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        ButterKnife.inject(this);
        Injector.inject(this);
        Bundle extras = getIntent().getExtras();
        score = extras.getInt("score");

        percents = (int) ((score/5.0) * 100);
        mProgress.setProgress(percents);
        percentage.setText(percents + "%");

        switch (score) {
            case 5:
                title.setText("AWESOME!");
                title.setTextColor(getResources().getColor(R.color.fbutton_color_amethyst));
                break;
            case 4:
                title.setText("GOOD :)");
                title.setTextColor(getResources().getColor(R.color.fbutton_color_green_sea));
                break;
            case 3:
                title.setText("NOT BAD!");
                break;
            case 2:
                title.setText("COULD BE BETTER");
                title.setTextColor(getResources().getColor(R.color.fbutton_color_wet_asphalt));
                break;
            default:
                title.setText("BAD :(");
                title.setTextColor(getResources().getColor(R.color.fbutton_color_pomegranate));
                break;
        }
    }

}
