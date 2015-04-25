package org.fairytail.guessthesong.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.model.game.Quiz;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GameFragment extends Fragment{

    @InjectView(R.id.flMain)
    FrameLayout flMain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO:addVariants(view, quiz);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public void addVariants(View view, Quiz quiz) {
        int position = view.getHeight() / 2;

        FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);

        for (int i = 0; i < quiz.getVariants().size(); i++) {
            Button btnVariant = new Button(this.getActivity());
            btnVariant.setText(quiz.getVariants().get(i).getArtist() + " - " + quiz.getVariants().get(i).getTitle());
            btnVariant.setY(position);

            final int finalI = i;
            btnVariant.setOnClickListener(v -> quiz.check(quiz.getVariants().get(finalI)));

            flMain.addView(btnVariant, lParams);
            position += 50;
        }
    }
}
