package com.tangramfactory.smartweight.activity.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Shlim on 2016-05-24.
 */
public class LevelAdapter extends FragmentStatePagerAdapter {

    private int pagerCount = 12;

    public LevelAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TextFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return pagerCount;
    }
}