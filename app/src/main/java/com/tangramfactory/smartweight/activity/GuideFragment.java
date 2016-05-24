package com.tangramfactory.smartweight.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.utility.DebugLogger;
import com.tangramfactory.smartweight.view.CircleIndicator;

/**
 * Created by B on 2016-05-23.
 */
public class GuideFragment extends Fragment {
    protected static final String TAG = SmartWeightApplication.BASE_TAG + "GuideFragment";

    ViewPager customViewpager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_guide, container, false);

        ViewPager customViewpager = (ViewPager)view.findViewById(R.id.viewpager_default);
        LevelAdapter customPagerAdapter = new LevelAdapter(getActivity().getSupportFragmentManager());
        customViewpager.setAdapter(customPagerAdapter);
        customViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int i2) {
            }

            @Override
            public void onPageSelected(int position) {
                DebugLogger.d(TAG, "Current selected = " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        CircleIndicator customIndicator = (CircleIndicator)view.findViewById(R.id.indicator_default);
        customIndicator.setViewPager(customViewpager);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
