package org.fairytail.guessthesong.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.activities.GameActivity;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.model.game.Quiz;
import org.fairytail.guessthesong.player.Player;

import java.util.ArrayList;
import java.util.List;
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

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    int pageNumber;
    ArrayList<Quiz> quizzes;
    Quiz quiz;
    int backColor;

    public static GameFragment newInstance(int page, ArrayList<Quiz> quizzes) {
        GameFragment pageFragment = new GameFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        arguments.putSerializable("quizzes", quizzes);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);

        Random rnd = new Random();
        backColor = Color.argb(60, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        ButterKnife.inject(this, view);
        tvPage.setText("Page " + pageNumber);
        gameLayout.setBackgroundColor(backColor);

        quizzes = (ArrayList<Quiz>) getArguments().getSerializable("quizzes");
        quiz = quizzes.get(pageNumber);
        addVariants(quiz);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Handler handler = new Handler();


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                player.prepare(quiz.getCorrectSong(), Player::start);
            }
        }, 1000);
    }

    public void addVariants(Quiz quiz) {
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < quiz.getVariants().size(); i++) {
            Button btnVariant = new Button(this.getActivity());
            btnVariant.setText(quiz.getVariants().get(i).getArtist() + " - " + quiz.getVariants().get(i).getTitle());

            final int finalI = i;

            btnVariant.setOnClickListener(v -> quiz.check(quiz.getVariants().get(finalI)));
            gameVariants.addView(btnVariant, lParams);

            ViewGroup.LayoutParams params = btnVariant.getLayoutParams();
            params.height = 150;
            btnVariant.setTextSize(TypedValue.COMPLEX_UNIT_PX, 35);
            btnVariant.setLayoutParams(params);
        }
    }

}