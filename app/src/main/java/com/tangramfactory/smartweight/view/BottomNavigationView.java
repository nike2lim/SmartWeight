package com.tangramfactory.smartweight.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tangramfactory.smartweight.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shlim on 2016-05-23.
 */
public class BottomNavigationView extends LinearLayout{

    private final int NAVIGATION_HEIGHT = (int) getResources().getDimension(R.dimen.bottom_navigation_height);
//    private final int NAVIGATION_LINE_WIDTH = (int) getResources().getDimension(R.dimen.bottom_navigation_line_width);
    private Context mContext;
    private FrameLayout container;


    private List<BottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    private List<View> viewList = new ArrayList<>();

    public BottomNavigationView(Context context) {
        this(context, null);
    }

    public BottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        container = new FrameLayout(context);
//        LinearLayout items = new LinearLayout(context);
//        items.setOrientation(LinearLayout.HORIZONTAL);
//        RelativeLayout.LayoutParams shadowParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, shadowHeight);
    }
}
