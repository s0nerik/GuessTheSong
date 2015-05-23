package org.fairytail.guessthesong.fragments.games;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eftimoff.androidplayer.actions.property.PropertyAction;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.adapters.GameAdapter;
import org.fairytail.guessthesong.custom_views.NonSwipeableViewPager;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.fragments.GamePageFragment;
import org.fairytail.guessthesong.player.Player;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class GameTwoFragment extends GamePageFragment{
    @Inject
    Player player;

    PropertyAction imgVinylAction;

    GameAdapter gAdapter;
    NonSwipeableViewPager gPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_two, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO:addVariants(view, quiz);
        //addVariantsTest(view, 6);

        //startAnimation();

//        final Handler handler = new Handler();
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//
//            }
//        }, 1000);
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


}
