package org.fairytail.guessthesong.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;


import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.events.QuizSongChosenEvent;
import org.fairytail.guessthesong.events.QuizTimeOverEvent;
import org.fairytail.guessthesong.model.Song;
import org.fairytail.guessthesong.model.game.Quiz;
import org.fairytail.guessthesong.player.Player;

import java.util.Random;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GameFragment extends Fragment {
    @InjectView(R.id.tvPage)
    TextView tvPage;
    @InjectView(R.id.game_variants)
    LinearLayout gameVariants;
    @InjectView(R.id.game_layout)
    FrameLayout gameLayout;

    @Inject
    Player player;

    static final String ARG_QUIZ = "quiz";

    QuizTimeOverEvent quizTimeOverEvent;
    Quiz quiz;
    int backColor;

    public static GameFragment newInstance(Quiz quiz) {
        GameFragment pageFragment = new GameFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_QUIZ, quiz);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            final Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    player.prepare(quiz.getCorrectSong(), Player::start);
                    quiz.start();
                }
            }, 1000);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        quiz = (Quiz) getArguments().getSerializable(ARG_QUIZ);

        Random rnd = new Random();
        backColor = Color.argb(60, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        ButterKnife.inject(this, view);
        tvPage.setText("Correct: " + quiz.getCorrectSong().getArtist() + " - " + quiz.getCorrectSong().getTitle());
        gameLayout.setBackgroundColor(backColor);

        addVariants(quiz);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void addVariants(Quiz quiz) {
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        for (Song s : quiz.getVariants()) {
            Button btnVariant = new Button(getActivity());
            btnVariant.setText(s.getArtist() + " - " + s.getTitle());

            //btnVariant.setOnClickListener(v -> quiz.check(s));
            btnVariant.setOnClickListener(view -> {
                //if(quiz.check(s)) {
                if (quiz.check(s)) {
                    btnVariant.setBackgroundColor(Color.parseColor("#00FF00"));
                } else {
                    btnVariant.setBackgroundColor(Color.parseColor("#FF0000"));
                }

            });

            gameVariants.addView(btnVariant, lParams);

            ViewGroup.LayoutParams params = btnVariant.getLayoutParams();
            params.height = 150;
            btnVariant.setTextSize(TypedValue.COMPLEX_UNIT_PX, 35);
            btnVariant.setLayoutParams(params);
        }
    }

}