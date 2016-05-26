package com.tangramfactory.smartweight.utility;

import android.content.res.Resources;

/**
 * Created by B on 2016-05-26.
 */
public class Utils {

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}

