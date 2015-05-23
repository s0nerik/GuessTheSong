package org.fairytail.guessthesong.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.eftimoff.androidplayer.actions.property.PropertyAction;

import org.fairytail.guessthesong.R;
import org.fairytail.guessthesong.adapters.GameAdapter;
import org.fairytail.guessthesong.custom_views.NonSwipeableViewPager;
import org.fairytail.guessthesong.model.game.Quiz;

import java.util.Random;

import butterknife.InjectView;

public class GamePageFragment extends Fragment {
    @InjectView(R.id.flMain)
    FrameLayout flMain;



    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    int pageNumber;
    int backColor;


    public static GameFragment newInstance(int num) {
        GameFragment gf = new GameFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt(ARGUMENT_PAGE_NUMBER, num);
        gf.setArguments(args);

        return gf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);

        Random rnd = new Random();
        backColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_page, container, false);

        TextView tvPage = (TextView) view.findViewById(R.id.game_title);
        tvPage.setText("Page " + pageNumber);
        tvPage.setBackgroundColor(backColor);

        return view;
    }


}