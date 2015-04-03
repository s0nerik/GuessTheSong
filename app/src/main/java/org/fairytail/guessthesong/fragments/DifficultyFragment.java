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
    @InjectView(R.id.easyButton)
    Button easyButton;
    @InjectView(R.id.normalButton)
    Button normalButton;
    @InjectView(R.id.hardButton)
    Button hardButton;

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        YoYo.with(Techniques.DropOut)
                .duration(1000)
                .playOn(difficultyTextView);

        YoYo.with(Techniques.BounceIn)
                .duration(1000)
                .playOn(easyButton);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
