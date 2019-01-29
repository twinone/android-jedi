package com.jediupc.helloandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

public class CanvasView extends View {

    Paint mPaint = new Paint();

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint.setColor(Color.parseColor("#ff0000"));
        mPaint.setStrokeWidth(2);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int h = getHeight();
        int w = getWidth();
        int smallest = Math.min(h, w);

        canvas.drawCircle(w / 2, h / 2, smallest / 2, mPaint);
        invalidate();
    }
}
