package com.tangramfactory.smartweight.activity.main;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.activity.workoutpreview.WorkoutPreviewActivity;
import com.tangramfactory.smartweight.utility.DebugLogger;
import com.tangramfactory.smartweight.view.CircleIndicator;
import com.tangramfactory.smartweight.view.RecyclerItemClickListener;
import com.tangramfactory.smartweight.vo.GuideStepVo;

import java.util.ArrayList;
import java.util.List;

import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import it.carlom.stikkyheader.core.animator.AnimatorBuilder;
import it.carlom.stikkyheader.core.animator.HeaderStikkyAnimator;

/**
 * Created by B on 2016-05-23.
 */
public class GuideFragment extends Fragment{
    protected static final String TAG = SmartWeightApplication.BASE_TAG + "GuideFragment";

    LinearLayout rootLayout;
    LinearLayout rootLayout2;
    LinearLayout levelViewPagerLayout;
    CircleIndicator customIndicator;
    ImageView headerImageView;
    ViewPager levelViewPager;
    RecyclerView recyclerView;
    GuideStepAdapter adapter;
    LinearLayout dayWorkoutLayout;
    LinearLayout dayExerciseLayout;
    LinearLayout lockScreenLayout;

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

        headerImageView = (ImageView)v.findViewById(R.id.header_image);
        rootLayout = (LinearLayout)v.findViewById(R.id.rootLayout);
//        rootLayout2 = (LinearLayout)v.findViewById(R.id.rootLayout2);

        oneDayText = (TextView)v.findViewById(R.id.oneday_text);
        twoDayText = (TextView)v.findViewById(R.id.twoday_text);
        threeDayText = (TextView)v.findViewById(R.id.threeday_text);

//        stepListLayout = (LinearLayout)v.findViewById(R.id.stepListLayout);

        levelViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int i2) {
            }

            @Override
            public void onPageSelected(int position) {
                DebugLogger.d(TAG, "Current selected = " + position);
                if(position%3 == 0) {
                    headerImageView.setBackgroundResource(R.drawable.level_03);
                }else if(position%2 == 0) {
                    headerImageView.setBackgroundResource(R.drawable.level_02);
                }else {
                    headerImageView.setBackgroundResource(R.drawable.level_01);
                }

                if(position > 0) {
                    showLockLevel();
                }else {
                    showLevel();
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

        customIndicator = (CircleIndicator)v.findViewById(R.id.indicator_default);
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

        dayWorkoutLayout = (LinearLayout)v.findViewById(R.id.day_workout_layout);
        dayExerciseLayout = (LinearLayout)v.findViewById(R.id.day_exercise_layout);
        lockScreenLayout =  (LinearLayout)v.findViewById(R.id.lock_screen_layout);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayout.LayoutParams params =  (LinearLayout.LayoutParams)levelViewPagerLayout.getLayoutParams();
                LinearLayout.LayoutParams params2 =  (LinearLayout.LayoutParams)customIndicator.getLayoutParams();
                LinearLayout.LayoutParams params3 =  (LinearLayout.LayoutParams)dayWorkoutLayout.getLayoutParams();

                if(dy > 0) {
                    float dp = params.topMargin/(getActivity().getResources().getDisplayMetrics().densityDpi/160);
                    DebugLogger.d(TAG, "onScrolled params  levelViewPagerLayout before params.topMargin dp = " + dp);
                    DebugLogger.d(TAG, "onScrolled params  levelViewPagerLayout before params.topMargin PIXEL = " + params.topMargin);
                    if(dp >= 63) {
                        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27,  getActivity().getResources().getDisplayMetrics());
//                        params.topMargin = params.topMargin - 27;
                        DebugLogger.d(TAG, "onScrolled levelViewPagerLayout after params.topMargin 27 dp to px = " + px);

                        params.topMargin = params.topMargin  - (int) px;
                        DebugLogger.d(TAG, "onScrolled levelViewPagerLayout after params.topMargin = " + params.topMargin);
                        levelViewPagerLayout.setLayoutParams(params);
                    }

                    float dp2 = params2.topMargin/(getActivity().getResources().getDisplayMetrics().densityDpi/160);

                    DebugLogger.d(TAG, "onScrolled params customIndicator before params.topMargin dp = " + dp2);
                    DebugLogger.d(TAG, "onScrolled params customIndicator before params.topMargin PIXEL = " + params2.topMargin);

                    if(dp2  >= 20) {
                        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11,  getActivity().getResources().getDisplayMetrics());
//                        params2.topMargin = params2.topMargin - 11;
                        DebugLogger.d(TAG, "onScrolled customIndicator after params.topMargin 11 dp to px = " + px);
                        params2.topMargin = (int) (params2.topMargin -px);
                        DebugLogger.d(TAG, "onScrolled customIndicator after params.topMargin = " + params2.topMargin);

                        customIndicator.setLayoutParams(params2);
                    }


                    float dp3 = params3.topMargin/(getActivity().getResources().getDisplayMetrics().densityDpi/160);

                    DebugLogger.d(TAG, "onScrolled params dayWorkoutLayout before params.topMargin dp = " + dp2);
                    DebugLogger.d(TAG, "onScrolled params dayWorkoutLayout before params.topMargin PIXEL = " + params3.topMargin);


                    if(dp3  >= 35) {
                        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,  getActivity().getResources().getDisplayMetrics());
//                        params2.topMargin = params2.topMargin - 11;
                        DebugLogger.d(TAG, "onScrolled dayWorkoutLayout after params.topMargin 11 dp to px = " + px);
                        params3.topMargin = (int) (params3.topMargin -px);
                        DebugLogger.d(TAG, "onScrolled dayWorkoutLayout after params.topMargin = " + params3.topMargin);

                        dayWorkoutLayout.setLayoutParams(params3);
                    }
                    dayExerciseLayout.setVisibility(View.GONE);
                }else {
                    if(headerImageView.getY() < 100) {
                        dayExerciseLayout.setVisibility(View.VISIBLE);

                        float dp = params.topMargin/(getActivity().getResources().getDisplayMetrics().densityDpi/160);
                        if(dp == 36) {
                            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27,  getActivity().getResources().getDisplayMetrics());
                            params.topMargin = params.topMargin + (int)px;
                            levelViewPagerLayout.setLayoutParams(params);
                        }

                        dp = params2.topMargin/(getActivity().getResources().getDisplayMetrics().densityDpi/160);
                        if(dp == 9) {
                            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11,  getActivity().getResources().getDisplayMetrics());
                            params2.topMargin = params2.topMargin + (int)px;
                            customIndicator.setLayoutParams(params2);
                        }

                        dp = params3.topMargin/(getActivity().getResources().getDisplayMetrics().densityDpi/160);
                        if(dp  == 20) {
                            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,  getActivity().getResources().getDisplayMetrics());
                            DebugLogger.d(TAG, "onScrolled customIndicator after params.topMargin 11 dp to px = " + px);
                            params3.topMargin = (int) (params3.topMargin +px);
                            DebugLogger.d(TAG, "onScrolled customIndicator after params.topMargin = " + params3.topMargin);

                            dayWorkoutLayout.setLayoutParams(params3);
                        }
                    }
                }
            }
        });
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

//            case R.id.scrollArrow:
//                stepListLayout.setVisibility(View.VISIBLE);
//                break;

            case R.id.exercise_1:
                Toast.makeText(getActivity(), "exercise_1 click!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.exercise_2:
                Toast.makeText(getActivity(), "exercise_2 click!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.exercise_3:
                Toast.makeText(getActivity(), "exercise_3 click!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.exercise_4:
                Toast.makeText(getActivity(), "exercise_4 click!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.exercise_5:
                Toast.makeText(getActivity(), "exercise_5 click!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.exercise_6:
                Toast.makeText(getActivity(), "exercise_6 click!", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        rootLayout2.setTranslationY(e2.getY() - e1.getY());
//        float scale = (rootLayout2.getHeight() / distanceY) / 100;
//        rootLayout2.setScaleY(scale);

        if(e2.getY() - e1.getY() < 0) {
//            if(stepListLayout.getVisibility() == View.GONE) {
//                stepListLayout.setVisibility(View.VISIBLE);
//            }

//            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams)rootLayout2.getLayoutParams();
//            if(param.height < 0) {
//                param.height = rootLayout2.getHeight();
//            }
//            param.height = (int) (param.height + (e2.getY() - e1.getY()));
//            rootLayout2.setLayoutParams(param);

            LinearLayout.LayoutParams params =  (LinearLayout.LayoutParams)levelViewPagerLayout.getLayoutParams();
            if(params.topMargin > 35) {
                params.topMargin = params.topMargin - 27;
                levelViewPagerLayout.setLayoutParams(params);
            }


        }else {
            LinearLayout.LayoutParams params =  (LinearLayout.LayoutParams)levelViewPagerLayout.getLayoutParams();
            params.topMargin = params.topMargin + 27;
            levelViewPagerLayout.setLayoutParams(params);
        }
        return false;
    };

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

    private void showLockLevel() {
        dayWorkoutLayout.setVisibility(View.GONE);
        dayExerciseLayout.setVisibility(View.GONE);
        lockScreenLayout.setVisibility(View.VISIBLE);
    }

    private void showLevel() {
        dayWorkoutLayout.setVisibility(View.VISIBLE);
        dayExerciseLayout.setVisibility(View.VISIBLE);
        lockScreenLayout.setVisibility(View.GONE);
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        StikkyHeaderBuilder.stickTo(recyclerView)
                .setHeader(R.id.header, (ViewGroup) getView())
                .minHeightHeaderDim(R.dimen.min_height_header)
                .animator(new ParallaxStikkyAnimator())
                .build();
    }

    private class ParallaxStikkyAnimator extends HeaderStikkyAnimator {
        @Override
        public AnimatorBuilder getAnimatorBuilder() {
            View mHeader_image = getHeader().findViewById(R.id.header_image);
            return AnimatorBuilder.create().applyVerticalParallax(mHeader_image);
        }
    }
}
