package com.tangramfactory.smartweight.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
public class GuideFragment extends Fragment{
    protected static final String TAG = SmartWeightApplication.BASE_TAG + "GuideFragment";

    LinearLayout rootLayout;
    LinearLayout levelViewPagerLayout;
    ViewPager levelViewPager;
    RecyclerView recyclerView;
    GuideStepAdapter adapter;

    TextView oneDayText;
    TextView twoDayText;
    TextView threeDayText;
    LinearLayout stepListLayout;

    int currentDay = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_guide, container, false);
        loadCodeView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadCodeView(View v) {
        levelViewPagerLayout = (LinearLayout)v.findViewById(R.id.viewPayer_layout);
        levelViewPager = (ViewPager)v.findViewById(R.id.viewpager_default);
        LevelAdapter customPagerAdapter = new LevelAdapter(getActivity().getSupportFragmentManager());
        levelViewPager.setAdapter(customPagerAdapter);
        rootLayout = (LinearLayout)v.findViewById(R.id.rootLayout);

        oneDayText = (TextView)v.findViewById(R.id.oneday_text);
        twoDayText = (TextView)v.findViewById(R.id.twoday_text);
        threeDayText = (TextView)v.findViewById(R.id.threeday_text);

        stepListLayout = (LinearLayout)v.findViewById(R.id.stepListLayout);

        levelViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int i2) {
            }

            @Override
            public void onPageSelected(int position) {
                DebugLogger.d(TAG, "Current selected = " + position);
                if(position%3 == 0) {
                    rootLayout.setBackgroundResource(R.drawable.level_03);
                }else if(position%2 == 0) {
                    rootLayout.setBackgroundResource(R.drawable.level_02);
                }else {
                    rootLayout.setBackgroundResource(R.drawable.level_01);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        levelViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        CircleIndicator customIndicator = (CircleIndicator)v.findViewById(R.id.indicator_default);
        customIndicator.setViewPager(levelViewPager);

        recyclerView = (RecyclerView)v.findViewById(R.id.guide_step_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);


        dummyGuideStepData();
        adapter = new GuideStepAdapter(dummyGuideStepList);
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

        LinearLayout dayWorkoutLayout = (LinearLayout)v.findViewById(R.id.day_workout_layout);
        LinearLayout dayExerciseLayout = (LinearLayout)v.findViewById(R.id.day_exercise_layout);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.oneday_text:
                initDayLayout();
                oneDayText.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                oneDayText.setBackgroundResource(R.drawable.days_sel);
                break;

            case R.id.twoday_text:
                initDayLayout();
                twoDayText.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                twoDayText.setBackgroundResource(R.drawable.days_sel);
                break;

            case R.id.threeday_text:
                initDayLayout();
                threeDayText.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                threeDayText.setBackgroundResource(R.drawable.days_sel);
                break;

            case R.id.scrollArrow:
                stepListLayout.setVisibility(View.VISIBLE);
                break;

        }

    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        return false;
    };

    private void initDayLayout() {
        oneDayText.setTextColor(Color.parseColor("#AAAAAA"));
        oneDayText.setBackgroundResource(0);
        twoDayText.setTextColor(Color.parseColor("#AAAAAA"));
        twoDayText.setBackgroundResource(0);
        threeDayText.setTextColor(Color.parseColor("#AAAAAA"));
        threeDayText.setBackgroundResource(0);
    }

    List<GuideStepVo> dummyGuideStepList = new ArrayList<GuideStepVo>();

    private void dummyGuideStepData() {

        GuideStepVo obj = new GuideStepVo("1", "2", "12", true);
        GuideStepVo obj2 = new GuideStepVo("2", "1", "10", false);
        GuideStepVo obj3 = new GuideStepVo("3", "0", "12", false);
        GuideStepVo obj4 = new GuideStepVo("4", "2", "15", false);
        GuideStepVo obj5 = new GuideStepVo("5", "1", "10", false);

        dummyGuideStepList.add(obj);
        dummyGuideStepList.add(obj2);
        dummyGuideStepList.add(obj3);
        dummyGuideStepList.add(obj4);
        dummyGuideStepList.add(obj5);
    }

}
