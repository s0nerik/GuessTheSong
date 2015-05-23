package org.fairytail.guessthesong.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eftimoff.androidplayer.actions.property.PropertyAction;
import com.joanzapata.android.asyncservice.api.annotation.InjectService;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.adapters.GameAdapter;
import org.fairytail.guessthesong.async.SongsGetterService;
import org.fairytail.guessthesong.custom_views.NonSwipeableViewPager;
import org.fairytail.guessthesong.dagger.Injector;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.model.game.Quiz;
import org.fairytail.guessthesong.player.Player;
import android.media.MediaPlayer;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class GameFragment extends Fragment{


    @Inject
    Player player;

    //PropertyAction imgVinylAction;

    static final int PAGE_COUNT = 10;

    NonSwipeableViewPager gPager;
    GameAdapter gAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    public boolean onPageSelected(int pageNumber) {
        // Just define a callback method in your fragment and call it like this!
        return gAdapter.getItem(pageNumber).isVisible();
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

        gPager = (NonSwipeableViewPager) view.findViewById(R.id.view_pager);
        gAdapter = new GameAdapter(getChildFragmentManager());
        gPager.setAdapter(gAdapter);

        gPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.d("MY_TAG", "onPageSelected, position = " + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

//        //TODO:addVariants(view, quiz);
//        addVariantsTest(view, 6);
//
//        imgVinylAction = PropertyAction.newPropertyAction(imgVinyl)
//                .interpolator(new DecelerateInterpolator())
//                .rotation(-3600)
//                .delay(1000)
//                .duration(25000)
//                .build();
//
//        com.eftimoff.androidplayer.Player.init()
//                .animate(imgVinylAction)
//                .play();

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

    private class GameAdapter extends FragmentPagerAdapter {

        public GameAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return GamePageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


}
