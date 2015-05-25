package org.fairytail.guessthesong.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.fairytail.guessthesong.fragments.GameFragment;
import org.fairytail.guessthesong.model.game.Game;

public class GameAdapter extends FragmentPagerAdapter {

    static final int PAGE_COUNT = 5;
    Game game;

    public GameAdapter(FragmentManager fm, Game g) {
        super(fm);
        game = g;
    }

    @Override
    public GameFragment getItem(int position) {
        return GameFragment.newInstance(game.getQuizzes().get(position));
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}

