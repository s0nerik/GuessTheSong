package org.fairytail.guessthesong.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.fairytail.guessthesong.fragments.GameFragment;
import org.fairytail.guessthesong.model.game.Game;
import org.fairytail.guessthesong.model.game.Quiz;

import java.util.ArrayList;

public class GameAdapter extends FragmentPagerAdapter {

    static final int PAGE_COUNT = 5;
    Game game;

    public GameAdapter(FragmentManager fm, Game g) {
        super(fm);
        game = g;
    }

    @Override
    public Fragment getItem(int position) {
        return GameFragment.newInstance(position, game.getQuizzes());
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}

