package org.fairytail.guessthesong.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.activities.GameActivity;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.events.PlaybackStateChangedEvent;
import org.fairytail.guessthesong.events.QuizSongChosenEvent;
import org.fairytail.guessthesong.model.Song;
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
    @InjectView(R.id.game_vinyl)
    ImageView gameVinyl;
    @InjectView(R.id.game_variants)
    LinearLayout gameVariants;
    @InjectView(R.id.game_layout)
    FrameLayout gameLayout;
    @InjectView(R.id.waiting)
    TextView waiting;

    @Inject
    Player player;

    @Inject
    Bus bus;

    static final String ARG_QUIZ = "quiz";

    Quiz quiz;
    int backColor;

    List<Button> buttons;

    public static GameFragment newInstance(Quiz quiz) {
        GameFragment pageFragment = new GameFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_QUIZ, quiz);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        bus.register(this);
        quiz = (Quiz) getArguments().getSerializable(ARG_QUIZ);

        Random rnd = new Random();
        backColor = Color.argb(60, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        ButterKnife.inject(this, view);
//        tvPage.setText("Correct: " + quiz.getCorrectSong().getArtist() + " - " + quiz.getCorrectSong().getTitle());
        gameLayout.setBackgroundColor(backColor);

        buttons = new ArrayList<>();
        addVariants(quiz);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (((GameActivity) getActivity()).isMultiplayer) {
            waiting.setVisibility(View.VISIBLE);
            gameVariants.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Subscribe
    public void onPlaybackStateChanged(PlaybackStateChangedEvent event) {
        if (((GameActivity) getActivity()).isMultiplayer && event.state == PlaybackStateChangedEvent.State.STARTED) {
            waiting.setVisibility(View.GONE);
            gameVariants.setVisibility(View.VISIBLE);
        }
    }

    public void addVariants(Quiz quiz) {
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        final Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(300);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(1);
        animation.setRepeatMode(Animation.REVERSE);

        for (Song s : quiz.getVariants()) {
            Button btnVariant = new Button(getActivity());
            btnVariant.setText(s.getArtist() + " - " + s.getTitle());

            btnVariant.setOnClickListener(view -> {
                if (quiz.check(s)) {
                    btnVariant.setBackgroundColor(Color.parseColor("#00FF00"));
                } else {
                    btnVariant.setBackgroundColor(Color.parseColor("#FF0000"));
                    btnVariant.startAnimation(animation);
                }

                for (Button b : buttons) {
                    b.setClickable(false);
                }

                bus.post(new QuizSongChosenEvent(quiz, s));
            });

            gameVariants.addView(btnVariant, lParams);

            ViewGroup.LayoutParams params = btnVariant.getLayoutParams();
            params.height = 150;
            btnVariant.setTextSize(TypedValue.COMPLEX_UNIT_PX, 35);
            btnVariant.setLayoutParams(params);

            buttons.add(btnVariant);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}