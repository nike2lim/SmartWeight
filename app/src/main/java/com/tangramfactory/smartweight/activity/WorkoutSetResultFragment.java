package com.tangramfactory.smartweight.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.tangramfactory.smartweight.vo.GuideResultVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shlim on 2016-05-28.
 */
public class WorkoutSetResultFragment extends BaseWorkoutResultFragment{
    protected static final String TAG = SmartWeightApplication.BASE_TAG + "WorkoutSetResultFragment";

    RecyclerView recyclerView;
    WorkoutSetResultAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_set_result, container, false);

        viewPager = (ViewPager)view.findViewById(R.id.viewpager_default);
        ViewPager customViewpager = (ViewPager)view.findViewById(R.id.viewpager_default);
        BadgeAdapter customPagerAdapter = new BadgeAdapter(getActivity().getSupportFragmentManager());
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

        recyclerView = (RecyclerView)view.findViewById(R.id.result_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);

        int badgeCount = 4;
        setDummyResultData();


        adapter = new WorkoutSetResultAdapter(dummyList);
        recyclerView.setAdapter(adapter);


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if((position+1) == adapter.getItemCount()) {
                    return;
                }

                DebugLogger.d(TAG, adapter.getItem(position).getExerciseType());
            }
        }));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    List<GuideResultVo> dummyList = new ArrayList<>();
    private void setDummyResultData() {

        GuideResultVo obj = new GuideResultVo(80, "BenchPress");
        obj.addSetInfo("1", "5", "kg", "15", "10", "90");
        obj.addSetInfo("2", "10", "kg", "12", "12", "80");
        obj.addSetInfo("3", "15", "kg", "8", "4", "70");


        GuideResultVo obj2 = new GuideResultVo(70, "Curl");
        obj2.addSetInfo("1", "5", "kg", "15", "10", "90");
        obj2.addSetInfo("2", "10", "kg", "12", "12", "80");
        obj2.addSetInfo("3", "15", "kg", "8", "4", "70");

        GuideResultVo obj3 = new GuideResultVo(90, "Cable Push Down ");
        obj3.addSetInfo("1", "5", "kg", "15", "10", "90");
        obj3.addSetInfo("2", "10", "kg", "12", "12", "80");
        obj3.addSetInfo("3", "15", "kg", "8", "4", "70");

//        GuideResultVo obj4 = new GuideResultVo(20, "Shoulder Press");
//        obj4.addSetInfo("1", "5", "kg", "15", "10", "90");
//        obj4.addSetInfo("2", "10", "kg", "12", "12", "80");
//        obj4.addSetInfo("3", "15", "kg", "8", "4", "70");

        dummyList.add(obj);
        dummyList.add(obj2);
        dummyList.add(obj3);
//        dummyList.add(obj4);

    }
}
