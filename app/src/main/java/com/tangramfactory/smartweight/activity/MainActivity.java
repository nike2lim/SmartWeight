package com.tangramfactory.smartweight.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.SmartWeightApplication;
import com.tangramfactory.smartweight.activity.base.BaseAppCompatActivity;

import java.util.ArrayList;


public class MainActivity extends BaseAppCompatActivity  {
    protected static final String TAG = SmartWeightApplication.BASE_TAG + "MainActivity";

    public Toolbar toolbar;
    private TabHost mTabHost;
    private FragmentTabHost mFragmentTabHost;
    ArrayList tabList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
        loadCodeView();
//        loadCodeView3();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_top_setting);
        toolbar.setTitle(R.string.title_main);
//        toolbar.setBackgroundColor(getResources().getColor(R.color.main_top));
        toolbar.findViewById(R.id.deviceBatteryState).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                startActivity(new Intent(mContext, DeviceSettingActivity.class));
            }
        });
        setSupportActionBar(toolbar);
    }

    private void loadCodeView() {
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        //Creating tab menu.
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
                HistoryFragment recordFragment = (HistoryFragment) fm.findFragmentByTag("Record");
                ProgressFragment progressFragment = (ProgressFragment) fm.findFragmentByTag("Progress");
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

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
                        ft.add(R.id.realtabcontent,new HistoryFragment(), "Record");
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
}
