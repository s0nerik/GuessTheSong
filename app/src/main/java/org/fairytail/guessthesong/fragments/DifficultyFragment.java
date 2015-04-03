package org.fairytail.guessthesong.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.fairytail.guessthesong.R;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);

                //Tada
                //BounceIn
                YoYo.with(Techniques.SlideInLeft)
                        .duration(1000)
                        .playOn(difficultyTextView);

                //Landing
                YoYo.with(Techniques.BounceIn)
                        .duration(2000)
                        .playOn(btnEasy);

                YoYo.with(Techniques.BounceIn)
                        .delay(250)
                        .duration(2000)
                        .playOn(btnNormal);

                YoYo.with(Techniques.BounceIn)
                        .delay(500)
                        .duration(2000)
                        .playOn(btnHard);
            }
        }, 1000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
