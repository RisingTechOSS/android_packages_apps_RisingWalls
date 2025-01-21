/*
 * Copyright (C)  2024 The RisingOS Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.risingos.wallpaperpicker.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.risingos.wallpaperpicker.R;

public class TextButtonView extends View {
    private String text;
    private int backgroundColor;
    private int textColor;
    private float scaleFactor = 1;
    private final Path path = new Path();

    public TextButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public TextButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ButtonView_common, 0, 0);

        backgroundColor = a.getColor(R.styleable.ButtonView_common_background_color, 0);
        textColor = a.getColor(R.styleable.ButtonView_common_foreground_color, 0);
        a.recycle();

        a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TextButtonView, 0, 0);
        text = a.getString(R.styleable.TextButtonView_text);
        a.recycle();

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ValueAnimator shrinkAnimator = new ValueAnimator();
                ValueAnimator enlargeAnimator = new ValueAnimator();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        shrinkAnimator = ValueAnimator.ofFloat(1f, 0.95f);
                        shrinkAnimator.setDuration(50);
                        shrinkAnimator.addUpdateListener(new SizeChangeAnimatorUpdateListener());
                        shrinkAnimator.start();
                        break;

                    case MotionEvent.ACTION_UP:
                        enlargeAnimator = ValueAnimator.ofFloat(0.95f, 1);
                        enlargeAnimator.setDuration(50);
                        enlargeAnimator.addUpdateListener(new SizeChangeAnimatorUpdateListener());
                        enlargeAnimator.start();
                        break;
                }
            return false;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = getHeight();
        int width = getWidth();

        int bottom = (int) (height * scaleFactor);
        int right = (int) ((width * scaleFactor));
        int left = (int) (((1 - scaleFactor) * width));
        int top = (int) ((1 - scaleFactor) * height);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(backgroundColor);

        path.reset();
        path.addRoundRect(left, top, right, bottom, 1000, 1000, Path.Direction.CW);
        path.setFillType(Path.FillType.EVEN_ODD);
        canvas.clipPath(path);
        canvas.drawPaint(paint);

        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize((float) ((height * 0.5) * scaleFactor));
        paint.setTypeface(getResources().getFont(R.font.family_outfit_regular));
        canvas.drawText(text, width / 2 ,  (int) ((height / 2) - ((paint.descent() + paint.ascent()) / 2)), paint);
    }

    private class SizeChangeAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
            scaleFactor = (float) valueAnimator.getAnimatedValue();
            invalidate();
        }
    }
}
