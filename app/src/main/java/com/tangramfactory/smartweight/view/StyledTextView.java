package com.tangramfactory.smartweight.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tangramfactory.smartweight.R;
import com.tangramfactory.smartweight.utility.DebugLogger;


@SuppressLint("Recycle")
public class StyledTextView extends TextView {

	private static final String path = "fonts/";
	private static final String TYPEFACE_NAME = "SmartRope.otf";

	public StyledTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		applyTypeface(context, attrs);
	}

	public StyledTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		applyTypeface(context, attrs);
	}

	public StyledTextView(Context context) {
		super(context);
	}

	private void applyTypeface(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.StyledTextView);
        String typefaceName = arr.getString(R.styleable.StyledTextView_typeface);
        if (typefaceName == null || typefaceName.isEmpty()) {
            typefaceName = TYPEFACE_NAME;
        }
        Typeface typeface = null;
        try {
            typeface = TypefaceClass.get(context, path+typefaceName);
            setTypeface(typeface);
        } catch (Exception e) {
            DebugLogger.e("FONT", e.getLocalizedMessage(), e);
        }
    }
}