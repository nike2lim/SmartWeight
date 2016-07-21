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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.activity.workoutpreview.WorkoutPreviewActivity;
import com.tangramfactory.smartweight.utility.DebugLogger;
import com.tangramfactory.smartweight.view.CircleIndicator;
import com.tangramfactory.smartweight.view.RecyclerItemClickListener;
import com.tangramfactory.smartweight.vo.GuideStepVo;
import com.tangramfactory.smartweight.vo.WorkoutVo;

import java.util.ArrayList;
import java.util.List;

import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import it.carlom.stikkyheader.core.StikkyHeaderRecyclerView;
import it.carlom.stikkyheader.core.animator.AnimatorBuilder;
import it.carlom.stikkyheader.core.animator.HeaderStikkyAnimator;


/**
 * Created by Shlim on 2016-05-23.
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

    StikkyHeaderBuilder.RecyclerViewBuilder builder;
    StikkyHeaderRecyclerView stikkyHeaderRecyclerView;

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
        DebugLogger.d(TAG, "GuideFragment loadCodeView!");

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
                if(position == 0) {
                    headerImageView.setImageResource(R.drawable.level_01);
                }else {
                    headerImageView.setImageResource(R.drawable.level_02);
                }
//                if(position%3 == 0) {
//                    headerImageView.setImageResource(R.drawable.level_03);
//                }else if(position%2 == 0) {
//                    headerImageView.setImageResource(R.drawable.level_02);
//                }else {
//                    headerImageView.setImageResource(R.drawable.level_01);
//                }

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


        customIndicator = (CircleIndicator)v.findViewById(R.id.indicator_default);
        customIndicator.setViewPager(levelViewPager);

        recyclerView = (RecyclerView)v.findViewById(R.id.guide_step_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);

        dummyGuideStepData();

        if(null == adapter) {
            adapter = new GuideStepAdapter(dummyGuideStepList);
        }

        if(null == recyclerView.getAdapter()) {
            recyclerView.setAdapter(adapter);
        }

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//				adapter.getItem(position)
                DebugLogger.d(TAG, adapter.getItem(position).getStepCount());

                Intent intent = new Intent(getActivity(), WorkoutPreviewActivity.class);
                intent.putExtra("stepNum", position+1);
                intent.putExtra("exerciseNum", 0);
                intent.putExtra("exerciseList", adapter.getItem(position).getWorkoutVo());
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
//                    dayExerciseLayout.setAlpha((float)0.5);
                    dayExerciseLayout.setVisibility(View.GONE);
                }else {
                    if(builder.getHeaderAnimator().getHeader().getY() >= (builder.getHeaderAnimator().getMaxTranslation() / 5)) {
                        dayExerciseLayout.setVisibility(View.VISIBLE);
//                        dayExerciseLayout.setAlpha((float)1);

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
//                Toast.makeText(getActivity(), "exercise_1 click!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.exercise_2:
//                Toast.makeText(getActivity(), "exercise_2 click!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.exercise_3:
//                Toast.makeText(getActivity(), "exercise_3 click!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.exercise_4:
//                Toast.makeText(getActivity(), "exercise_4 click!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.exercise_5:
//                Toast.makeText(getActivity(), "exercise_5 click!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.exercise_6:
//                Toast.makeText(getActivity(), "exercise_6 click!", Toast.LENGTH_SHORT).show();
                break;

        }
    }

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

        builder.getHeaderAnimator().onScroll(0);
        stikkyHeaderRecyclerView.initDecoration();
        recyclerView.setAdapter(null);
        recyclerView.removeAllViews();

        recyclerGoneSetLayoutParam();
    }

    private void showLevel() {
        dayWorkoutLayout.setVisibility(View.VISIBLE);
        dayExerciseLayout.setVisibility(View.VISIBLE);
        lockScreenLayout.setVisibility(View.GONE);

        builder.getHeaderAnimator().onScroll(0);
        builder.minHeightHeaderDim(R.dimen.min_height_header);
        recyclerView.setAdapter(adapter);
        stikkyHeaderRecyclerView.initDecoration();
        stikkyHeaderRecyclerView.init();

        recyclerGoneSetLayoutParam();
    }

    private void recyclerShowSetLayoutParam() {
        LinearLayout.LayoutParams params =  (LinearLayout.LayoutParams)levelViewPagerLayout.getLayoutParams();
        LinearLayout.LayoutParams params2 =  (LinearLayout.LayoutParams)customIndicator.getLayoutParams();
        LinearLayout.LayoutParams params3 =  (LinearLayout.LayoutParams)dayWorkoutLayout.getLayoutParams();

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
        dayExerciseLayout.setAlpha((float)0.5);
    }

    private void recyclerGoneSetLayoutParam() {

        LinearLayout.LayoutParams params =  (LinearLayout.LayoutParams)levelViewPagerLayout.getLayoutParams();
        LinearLayout.LayoutParams params2 =  (LinearLayout.LayoutParams)customIndicator.getLayoutParams();
        LinearLayout.LayoutParams params3 =  (LinearLayout.LayoutParams)dayWorkoutLayout.getLayoutParams();

        dayExerciseLayout.setVisibility(View.VISIBLE);
        dayExerciseLayout.setAlpha((float)1);

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

    List<GuideStepVo> dummyGuideStepList = new ArrayList<GuideStepVo>();

    private void dummyGuideStepData() {
        if(dummyGuideStepList.size() > 0)       return;

        dummyWorkoutData();

        GuideStepVo obj = new GuideStepVo("1", "2", "12", true, dummyWorkoutList);
        GuideStepVo obj2 = new GuideStepVo("2", "1", "10", false, dummyWorkoutList);
        GuideStepVo obj3 = new GuideStepVo("3", "0", "12", false, dummyWorkoutList);
        GuideStepVo obj4 = new GuideStepVo("4", "2", "15", false, dummyWorkoutList);
        GuideStepVo obj5 = new GuideStepVo("5", "1", "10", false, dummyWorkoutList);

        dummyGuideStepList.add(obj);
        dummyGuideStepList.add(obj2);
        dummyGuideStepList.add(obj3);
        dummyGuideStepList.add(obj4);
        dummyGuideStepList.add(obj5);
    }


    List<WorkoutVo> dummyWorkoutList = new ArrayList<WorkoutVo>();

    private void dummyWorkoutData() {
//        1. 컬
//        2. 레터럴 레이즈
//        3. 숄더 프레스
//        4. 킥백
//        5. 런지

        WorkoutVo obj = new WorkoutVo(getString(R.string.text_curl),"Dumbell", 10, "5", "lbs", 10, "\"android.resource://\" + getPackageName() + \"/\"+R.raw.bench_press", 3);
//        WorkoutVo obj2 = new WorkoutVo(getString(R.string.text_lateral_raises),"Dumbell", 10, "5", "lbs", 60, "\"android.resource://\" + getPackageName() + \"/\"+R.raw.bench_press", 1);
//        WorkoutVo obj3 = new WorkoutVo(getString(R.string.text_shoulder_press),"Dumbell", 10, "5", "lbs", 60, "\"android.resource://\" + getPackageName() + \"/\"+R.raw.bench_press", 1);
//        WorkoutVo obj4 = new WorkoutVo(getString(R.string.text_kick_back),"Dumbell", 10, "5", "lbs", 60, "\"android.resource://\" + getPackageName() + \"/\"+R.raw.bench_press", 1);
//        WorkoutVo obj5= new WorkoutVo(getString(R.string.text_lunge),"Dumbell", 10, "5", "lbs", 60, "\"android.resource://\" + getPackageName() + \"/\"+R.raw.bench_press", 1);

        dummyWorkoutList.add(obj);
//        dummyWorkoutList.add(obj2);
//        dummyWorkoutList.add(obj3);
//        dummyWorkoutList.add(obj4);
//        dummyWorkoutList.add(obj5);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        builder = StikkyHeaderBuilder.stickTo(recyclerView);
        builder.setHeader(R.id.header, (ViewGroup) getView());
        builder.minHeightHeaderDim(R.dimen.min_height_header);
        stikkyHeaderRecyclerView = builder.build();
    }

    private class ParallaxStikkyAnimator extends HeaderStikkyAnimator {
        @Override
        public AnimatorBuilder getAnimatorBuilder() {
            View mHeader_image = getHeader().findViewById(R.id.header_image);
            return AnimatorBuilder.create().applyVerticalParallax(mHeader_image);
        }
    }

}
