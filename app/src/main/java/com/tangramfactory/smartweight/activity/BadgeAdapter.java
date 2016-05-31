package com.tangramfactory.smartweight.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by shlim on 2016-05-28.
 */
public class BadgeAdapter  extends FragmentStatePagerAdapter {

    private int pagerCount = 12;

    public BadgeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return ImageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return pagerCount;
    }

    public void setCount(int count) {
        pagerCount = count;
    }
}