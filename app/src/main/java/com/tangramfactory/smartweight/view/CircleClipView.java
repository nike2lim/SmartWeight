package com.tangramfactory.smartweight.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


import com.tangramfactory.smartweight.R;

/**
 * Created by B on 2016-05-27.
 */
public class CircleClipView extends View {
    private Context mContext;
    private Bitmap mBitmap;

    private final int DEAFAULT_START_DEGREE = 0;
    private final int DEAFAULT_CLIP_DEGREE = 0;

    Paint paint;
    private Path mClippingPath;
    private int resourceID;
    private float clipAngle = (float)0;
    private float startAngle = (float)DEAFAULT_START_DEGREE;

    public CircleClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initilize(attrs);
    }

    private void initilize(AttributeSet attrs) {
        mClippingPath = new Path();
        paint = new Paint();
        paint.setColor(Color.parseColor("#FF0000"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(40);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleClip);
        resourceID = typedArray.getResourceId(R.styleable.CircleClip_bg, R.drawable.angle_bar);
        startAngle = typedArray.getInt(R.styleable.CircleClip_start_degree, DEAFAULT_START_DEGREE);
        clipAngle = typedArray.getInt(R.styleable.CircleClip_clip_degree, DEAFAULT_CLIP_DEGREE);
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceID);
    }

    public void setClippingAngle(float angle) {
        this.clipAngle = angle;
        invalidate();
    }
    public float getClippingAngle() {
        return clipAngle;
    }

    public void setStartAngle(float angle) {
        this.startAngle = angle;
    }

    public float getStartAngle() {
        return startAngle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float angle = (clipAngle);
        mClippingPath.reset();

        RectF oval = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        mClippingPath.moveTo(oval.centerX(), oval.centerY());
        mClippingPath.addArc(oval, startAngle, angle - startAngle);
        mClippingPath.lineTo(oval.centerY(), oval.centerX());

        //Clip the canvas
        canvas.clipPath(mClippingPath);
//        canvas.drawArc(oval, (float)0, angle, false, paint);
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBitmap != null &&  !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
