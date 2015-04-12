package org.fairytail.guessthesong.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.dagger.Injector;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DifficultyFragment extends Fragment {

    @InjectView(R.id.difficultyTextView)
    TextView difficultyTextView;
    @InjectView(R.id.btn_easy)
    Button btnEasy;
    @InjectView(R.id.btn_normal)
    Button btnNormal;
    @InjectView(R.id.btn_hard)
    Button btnHard;

    @Inject
    WindowManager windowManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_difficulty, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Handler handler = new Handler();

        difficultyTextView.setVisibility(View.INVISIBLE);
        btnEasy.setVisibility(View.INVISIBLE);
        btnNormal.setVisibility(View.INVISIBLE);
        btnHard.setVisibility(View.INVISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                difficultyTextView.setVisibility(View.VISIBLE);
                btnEasy.setVisibility(View.VISIBLE);
                btnNormal.setVisibility(View.VISIBLE);
                btnHard.setVisibility(View.VISIBLE);

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
                        float height = view.getHeight();
                        float width = view.getWidth();

                        float translationLeftToRight = value * (width / 2);
                        float translationRightToLeft = value * -width + 3*(width / 2);
                        float translationY = value * (height / 8);

                        difficultyTextView.setY(translationY);

                        btnEasy.setX(translationLeftToRight - (btnEasy.getWidth() / 2));
                        btnNormal.setX(translationRightToLeft - (btnNormal.getWidth() / 2));
                        btnHard.setX(translationLeftToRight - (btnHard.getWidth() / 2));
                    }
                });

                // Set the spring in motion; moving from 0 to 1
                spring.setEndValue(1);
            }
        }, 500);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
