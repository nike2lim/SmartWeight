package com.tangramfactory.smartweight.activity.workoutresult;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;
import com.tangramfactory.smartweight.activity.device.CmdConst;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkoutResultActivity extends BaseAppCompatActivity {

    protected static final String TAG = SmartWeightApplication.BASE_TAG + "CompetitionActivity";
    public Toolbar toolbar;
    ViewPager viewPager;
    FragmentPagerItems pages;
    FragmentPagerItemAdapter adapter;

    protected DateTime currentDate = DateTime.now();
    protected Date currDate = new Date();

    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_result);
        setToolbar();
        loadCodeView();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        String displayDay;

        simpleDateFormat =  new SimpleDateFormat(getString(R.string.text_diplay_date), Locale.getDefault());
        displayDay = simpleDateFormat.format(currDate);

//        DateTimeFormatter displayFormat = DateTimeFormat.forPattern(getString(R.string.text_diplay_date));
//        displayDay = displayFormat.print(currentDate);
        toolbar.setTitle(displayDay);
        toolbar.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.mGuideResultVo.clear();
                finish();
            }
        });
        setSupportActionBar(toolbar);
    }

    protected void loadCodeView() {
        stopWorkoutCmd();

        ViewGroup tab = (ViewGroup) findViewById(R.id.tab);
        tab.addView(LayoutInflater.from(this).inflate(R.layout.fragment_viewpager, tab, false));

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);

        pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of(getString(R.string.text_set), WorkoutSetResultFragment.class));
        pages.add(FragmentPagerItem.of(getString(R.string.text_total), WorkoutTotalResultFragment.class));

        adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);

        String listType = getIntent().getStringExtra("listType");
        if(!TextUtils.isEmpty(listType)){
            if("Set".equals(listType)){
                viewPager.setCurrentItem(0);
            } else {
                viewPager.setCurrentItem(1);
            }
        }
    }

    @Override
    public void onBackPressed() {
        mApplication.mGuideResultVo.clear();
        super.onBackPressed();
    }

    public  void stopWorkoutCmd() {
        mApplication.send(CmdConst.CMD_REQUEST_STOP, (byte)0, null);
    }
}
