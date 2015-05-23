package org.fairytail.guessthesong.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.fairytail.guessthesong.fragments.GameFragment;
import org.fairytail.guessthesong.fragments.GamePageFragment;
import org.fairytail.guessthesong.fragments.games.GameFiveFragment;
import org.fairytail.guessthesong.fragments.games.GameFourFragment;
import org.fairytail.guessthesong.fragments.games.GameOneFragment;
import org.fairytail.guessthesong.fragments.games.GameThreeFragment;
import org.fairytail.guessthesong.fragments.games.GameTwoFragment;

public class GameAdapter extends FragmentPagerAdapter {

    private static final int NUM_GAMES = 5;

    public GameAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return NUM_GAMES;
    }

    @Override
    public Fragment getItem(int position) {
        return GamePageFragment.newInstance(position);
//        switch (position) {
//            case 0:
//                return GameOneFragment.newInstance(position);
//            case 1:
//                return GameTwoFragment.newInstance(position);
//            case 2:
//                return GameThreeFragment.newInstance(position);
//            case 3:
//                return GameFourFragment.newInstance(position);
//            case 4:
//                return GameFiveFragment.newInstance(position);
//            default:
//                return GameOneFragment.newInstance(position);
//        }
    }
}