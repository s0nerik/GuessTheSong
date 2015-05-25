package org.fairytail.guessthesong.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.eftimoff.androidplayer.actions.property.PropertyAction;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.joanzapata.android.asyncservice.api.annotation.InjectService;
import com.joanzapata.android.asyncservice.api.annotation.OnMessage;
import com.joanzapata.android.asyncservice.api.internal.AsyncService;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.activities.GameActivity;
import org.fairytail.guessthesong.async.SongsGetterService;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.db.Order;
import org.fairytail.guessthesong.events.QuizTimeOverEvent;
import org.fairytail.guessthesong.model.game.Difficulty;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.player.Player;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DifficultyFragment extends Fragment {

    @InjectView(R.id.img_notes)
    ImageView imgNotes;
    @InjectView(R.id.btn_easy)
    Button btnEasy;
    @InjectView(R.id.btn_normal)
    Button btnNormal;
    @InjectView(R.id.btn_hard)
    Button btnHard;
    @InjectView(R.id.r_layout_diff_header)
    RelativeLayout rLayoutDiffHeader;
    @InjectView(R.id.progress_view)
    CircularProgressView progressView;

    @Inject
    WindowManager windowManager;

    @Inject
    Player player;

    @InjectService
    SongsGetterService songsGetterService;

    PropertyAction imgNotesAction;
    PropertyAction headerAction;
    float headerHeight;
    int diffId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        AsyncService.inject(this);
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

        headerHeight = getResources().getDimension(R.dimen.diff_header_translation);

        imgNotesAction = PropertyAction.newPropertyAction(imgNotes)
                .alpha(0f)
                .delay(400)
                .duration(600)
                .build();

        headerAction = PropertyAction.newPropertyAction(rLayoutDiffHeader)
                .interpolator(new DecelerateInterpolator())
                .translationY(-headerHeight)
                .delay(800)
                .duration(500)
                .build();

        com.eftimoff.androidplayer.Player.init()
                .animate(imgNotesAction)
                .animate(headerAction)
                .play();

        final Handler handler = new Handler();

        btnEasy.setVisibility(View.INVISIBLE);
        btnNormal.setVisibility(View.INVISIBLE);
        btnHard.setVisibility(View.INVISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
                        float width = view.getWidth();

                        float translationLeftToRight = value * (width / 2);
                        float translationRightToLeft = value * -width + 3*(width / 2);

                        btnEasy.setX(translationLeftToRight - (btnEasy.getWidth() / 2));
                        btnNormal.setX(translationRightToLeft - (btnNormal.getWidth() / 2));
                        btnHard.setX(translationLeftToRight - (btnHard.getWidth() / 2));
                    }
                });

                // Set the spring in motion; moving from 0 to 1
                spring.setEndValue(1);
            }
        }, 400);
    }

    @OnMessage
    public void onSongsAvailable(SongsGetterService.SongsListLoadedEvent e) {
        Intent i = new Intent(getActivity(), GameActivity.class);
        Bundle b = new Bundle();

        switch (diffId) {
            case 0:
                b.putSerializable("game", new Game.Creator().create(Difficulty.Level.EASY, new ArrayList<>(e.getSongs().subList(0, 5)), new ArrayList<>(e.getSongs())));
                break;
            case 1:
                b.putSerializable("game", new Game.Creator().create(Difficulty.Level.MEDIUM, new ArrayList<>(e.getSongs().subList(0, 5)), new ArrayList<>(e.getSongs())));
                break;
            case 2:
                b.putSerializable("game", new Game.Creator().create(Difficulty.Level.HARD, new ArrayList<>(e.getSongs().subList(0, 5)), new ArrayList<>(e.getSongs())));
                break;
        }

        progressView.setIndeterminate(false);

        i.putExtras(b);
        startActivity(i);

//        player.prepare(e.getSongs().get(0), Player::start);

    }

    @OnClick(R.id.btn_easy)
    public void onEasyClicked() {
        songsGetterService.loadAllSongs(Order.RANDOM);
        progressView.setIndeterminate(true);
        diffId = 0;
    }

    @OnClick(R.id.btn_normal)
    public void onNormalClicked() {
        songsGetterService.loadAllSongs(Order.RANDOM);
        progressView.setIndeterminate(true);
        diffId = 1;
    }

    @OnClick(R.id.btn_hard)
    public void onHardClicked() {
        songsGetterService.loadAllSongs(Order.RANDOM);
        progressView.setIndeterminate(true);
        diffId = 2;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}