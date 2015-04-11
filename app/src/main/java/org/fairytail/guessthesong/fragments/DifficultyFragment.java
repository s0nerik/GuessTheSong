package org.fairytail.guessthesong.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

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

//        view.setVisibility(View.VISIBLE);
//
//        int width = windowManager.getDefaultDisplay().getWidth();
//        int height = windowManager.getDefaultDisplay().getHeight();
//
//        ObjectAnimator animation1 = ObjectAnimator.ofFloat(btnEasy, "x", 0,
//                width / 2);
//        animation1.setDuration(1400);
//        ObjectAnimator animation2 = ObjectAnimator.ofFloat(btnNormal, "y", 0,
//                height / 2);
//        animation2.setDuration(1400);
//        AnimatorSet set = new AnimatorSet();
//        set.playTogether(animation1, animation2);
//        set.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
