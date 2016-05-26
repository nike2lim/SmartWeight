package com.tangramfactory.smartweight.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.utility.DebugLogger;
import com.tangramfactory.smartweight.view.CircleIndicator;
import com.tangramfactory.smartweight.view.RecyclerItemClickListener;
import com.tangramfactory.smartweight.vo.GuideStepVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by B on 2016-05-23.
 */
public class GuideFragment extends Fragment {
    protected static final String TAG = SmartWeightApplication.BASE_TAG + "GuideFragment";

    ViewPager customViewpager;
    RecyclerView recyclerView;
    GuideStepAdapter adapter;

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


        recyclerView = (RecyclerView)view.findViewById(R.id.guide_step_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);

        List<GuideStepVo> list = new ArrayList<GuideStepVo>();

        GuideStepVo obj = new GuideStepVo("1", "2", "12", true);
        GuideStepVo obj2 = new GuideStepVo("2", "1", "10", false);
        GuideStepVo obj3 = new GuideStepVo("3", "0", "12", false);
        GuideStepVo obj4 = new GuideStepVo("4", "2", "15", false);
        GuideStepVo obj5 = new GuideStepVo("5", "1", "10", false);

        list.add(obj);
        list.add(obj2);
        list.add(obj3);
        list.add(obj4);
        list.add(obj5);
        adapter = new GuideStepAdapter(list);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//				adapter.getItem(position)
                DebugLogger.d(TAG, adapter.getItem(position).getStepCount());
                Intent intent = new Intent(getActivity(), WorkoutPreviewActivity.class);
                
//                intent.putExtra("title", adapter.getItem(position).getTitle());
//                intent.putExtra("resId", adapter.getItem(position).getFileResId());
                startActivity(intent);
            }
        }));

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
