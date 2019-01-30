package com.jediupc.helloandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CanvasView extends View implements View.OnTouchListener {

    Paint mPaint = new Paint();
    private float mX;
    private float mY;
    private float mFingerX;
    private float mFingerY;
    private boolean mPressed;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint.setColor(Color.parseColor("#ff0000"));
        mPaint.setStrokeWidth(2);

        setOnTouchListener(this);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int h = getHeight();
        int w = getWidth();
        int smallest = Math.min(h, w);

        int factor = smallest / 2 / 10;

        if (mPressed) {
            canvas.drawCircle(mFingerX, mFingerY, smallest / 8, mPaint);
        } else {
            canvas.drawCircle(w / 2 + mX * factor, h / 2 + mY * factor, smallest / 8, mPaint);
        }
        invalidate();
    }

    public void setXY(float x, float y) {
        mX = x;
        mY = y;
    }

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        }

        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            mFingerX = e.getX();
            mFingerY = e.getY();
            mPressed = true;
        }

        if (e.getAction() == MotionEvent.ACTION_UP) {
            mPressed = false;
        }

        return false;
    }
}
