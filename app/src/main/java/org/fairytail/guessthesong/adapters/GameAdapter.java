package org.fairytail.guessthesong.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.fairytail.guessthesong.fragments.GameFragment;

public class GameAdapter extends FragmentPagerAdapter {

    static final int PAGE_COUNT = 10;

    public GameAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return GameFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}

