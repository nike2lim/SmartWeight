package com.tangramfactory.smartweight.activity;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by B on 2016-05-23.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new GuideFragment();

            case 1:
                return new SelfFragment();
            case 2:
                return new HistoryFragment();
            case 3:
                return new ProgressFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
// get item count - equal to number of tabs
        return 4;
    }
}
