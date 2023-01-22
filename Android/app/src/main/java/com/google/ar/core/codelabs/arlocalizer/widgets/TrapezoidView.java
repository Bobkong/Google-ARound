package com.google.ar.core.codelabs.arlocalizer.widgets;

import android.content.Context;

import android.graphics.Canvas;

import android.graphics.Color;

import android.graphics.Paint;

import android.graphics.Path;

import android.graphics.drawable.ShapeDrawable;

import android.graphics.drawable.shapes.PathShape;

import android.util.AttributeSet;

import android.view.View;

import com.google.ar.core.codelabs.arlocalizer.R;

public class TrapezoidView extends View {

    private ShapeDrawable mTrapezoid;

    public TrapezoidView(Context context, AttributeSet attrs) {

        super(context, attrs);

        Path path = new Path();

        path.moveTo(0.0f, 0.0f);

        path.lineTo(130.0f, 0.0f); // rt

        path.lineTo(200.0f, 100.0f); //rb

        path.lineTo(0.0f, 100.0f); //lb

        path.lineTo(70.0f, 0.0f); //lt

        mTrapezoid = new ShapeDrawable(new PathShape(path, 200.0f, 100.0f));

        mTrapezoid.getPaint().setStyle(Paint.Style.FILL);

        mTrapezoid.getPaint().setColor(context.getColor(R.color.transparent_60_theme_color));

    }

    @Override

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        mTrapezoid.setBounds(0, 0, w, h);

    }

    @Override

    protected void onDraw(Canvas canvas) {

        mTrapezoid.draw(canvas);

    }

}