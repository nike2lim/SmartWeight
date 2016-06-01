package com.tangramfactory.smartweight.activity;


import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;
import com.tangramfactory.smartweight.utility.DebugLogger;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseAppCompatActivity  implements GestureDetector.OnGestureListener{
    protected static final String TAG = SmartWeightApplication.BASE_TAG + "MainActivity";

    public Toolbar toolbar;
    private TabHost mTabHost;
    GestureDetector mGesDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
        loadCodeView();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_top_setting);
        toolbar.setTitle(R.string.title_main);
        toolbar.findViewById(R.id.deviceBatteryState).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ScanActivity.class));
            }
        });
        setSupportActionBar(toolbar);
    }

    private void loadCodeView() {
        mGesDetector = new GestureDetector(mContext, this);
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabHost.TabSpec tabMenu1 = mTabHost.newTabSpec("Guide");
        TabHost.TabSpec tabMenu2 = mTabHost.newTabSpec("Self");
        TabHost.TabSpec tabMenu3 = mTabHost.newTabSpec("Record");
        TabHost.TabSpec tabMenu4 = mTabHost.newTabSpec("Progress");

        ArrayList<TabHost.TabSpec> tabList = new ArrayList<>();
        tabList.add(tabMenu1);
        tabList.add(tabMenu2);
        tabList.add(tabMenu3);
        tabList.add(tabMenu4);

        int[] tabDrawableList = {R.drawable.selector_tab_guide, R.drawable.selector_tab_self, R.drawable.selector_tab_history, R.drawable.selector_tab_progress};

        for(int i = 0; i < tabList.size(); i++) {
            View indicator = getLayoutInflater().inflate(R.layout.bottombar_layout,null);
            ImageView imageView = (ImageView) indicator.findViewById(R.id.icon);
            imageView.setBackgroundResource(tabDrawableList[i]);
            TabHost.TabSpec tabMenu = tabList.get(i);
            tabMenu.setIndicator(indicator);
            tabMenu.setContent(new DummyTabContent(mContext));
        }

        mTabHost.addTab(tabMenu1);
        mTabHost.addTab(tabMenu2);
        mTabHost.addTab(tabMenu3);
        mTabHost.addTab(tabMenu4);

        android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();
        GuideFragment guideFragment = (GuideFragment) fm.findFragmentByTag("Guide");
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

        if(guideFragment!=null)
            ft.detach(guideFragment);

        if(guideFragment==null){
            ft.add(R.id.realtabcontent,new GuideFragment(), "Guide");
        }else{
            ft.attach(guideFragment);
        }
        ft.commit();

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();
                GuideFragment guideFragment = (GuideFragment) fm.findFragmentByTag("Guide");
                SelfFragment selfFragment = (SelfFragment) fm.findFragmentByTag("Self");
                RecordFragment recordFragment = (RecordFragment) fm.findFragmentByTag("Record");
                ProgressFragment progressFragment = (ProgressFragment) fm.findFragmentByTag("Progress");
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

                List<android.support.v4.app.Fragment> fragmentList =  fm.getFragments();
                for(int i= 0; i <  fragmentList.size(); i++){
                    android.support.v4.app.Fragment fragment = fragmentList.get(i);
                    if(fragment instanceof TextFragment) {
                        ft.detach(fragment);
                    }
                }

                if(guideFragment!=null)
                    ft.detach(guideFragment);

                if(selfFragment!=null)
                    ft.detach(selfFragment);

                if(recordFragment!=null)
                    ft.detach(recordFragment);

                if(progressFragment!=null)
                    ft.detach(progressFragment);

                if(tabId.equalsIgnoreCase("Guide")){
                    if(guideFragment==null){
                        ft.add(R.id.realtabcontent,new GuideFragment(), "Guide");
                    }else{
                        ft.attach(guideFragment);
                    }

                }else if(tabId.equalsIgnoreCase("Self")) {
                    if(selfFragment==null){
                        ft.add(R.id.realtabcontent,new SelfFragment(), "Self");
                    }else{
                        ft.attach(selfFragment);
                    }

                }else if(tabId.equalsIgnoreCase("Record")) {
                    if(recordFragment==null){
                        ft.add(R.id.realtabcontent,new RecordFragment(), "Record");
                    }else{
                        ft.attach(recordFragment);
                    }
                }else{
                    if(progressFragment==null){
                        ft.add(R.id.realtabcontent,new ProgressFragment(), "Progress");
                    }else{
                        ft.attach(progressFragment);
                    }
                }
                ft.commit();
            }
        });

    }

    public class DummyTabContent implements TabHost.TabContentFactory {
        private Context mContext;

        public DummyTabContent(Context context){
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            return v;
        }
    }

    public void onViewClick(View v) {
        String tabTag = mTabHost.getCurrentTabTag();
        android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();

        if(tabTag.equals("Guide")) {
            GuideFragment guideFragment = (GuideFragment) fm.findFragmentByTag("Guide");
            guideFragment.onViewClick(v);
        }else if(tabTag.equals("Self")) {
            SelfFragment selfFragment = (SelfFragment) fm.findFragmentByTag("Self");
            selfFragment.onViewClick(v);
        }else if(tabTag.equals("Record")) {
            RecordFragment recordFragment = (RecordFragment) fm.findFragmentByTag("Record");
            recordFragment.onViewClick(v);
        }else if(tabTag.equals("Progress")) {
            ProgressFragment progressFragment = (ProgressFragment) fm.findFragmentByTag("Progress");
            progressFragment.onViewClick(v);
        }else {

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGesDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        DebugLogger.d(TAG, "GuideFragment onDown!");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        DebugLogger.d(TAG, "GuideFragment onShowPress!");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        DebugLogger.d(TAG, "GuideFragment onSingleTapUp!");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        DebugLogger.d(TAG, "GuideFragment onScroll!");
        String tabTag = mTabHost.getCurrentTabTag();
        android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();

        if(tabTag.equals("Guide")) {
            GuideFragment guideFragment = (GuideFragment) fm.findFragmentByTag("Guide");
            guideFragment.onScroll(e1,e2,distanceX, distanceY);
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        DebugLogger.d(TAG, "GuideFragment onLongPress!");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        DebugLogger.d(TAG, "GuideFragment onFling!");
        String tabTag = mTabHost.getCurrentTabTag();
        android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();

        if(tabTag.equals("Guide")) {
            GuideFragment guideFragment = (GuideFragment) fm.findFragmentByTag("Guide");
            guideFragment.onFling(e1,e2,velocityX, velocityY);
        }
        return false;
    }
}
