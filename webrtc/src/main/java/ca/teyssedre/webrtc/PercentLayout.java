/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Pierre Teyssedre
 * Created by  :  pteyssedre
 * Date        :  15-11-18
 * Application :  Paranoya .
 * Package     :  ca.teyssedre.webrtc .
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ca.teyssedre.webrtc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class PercentLayout extends ViewGroup {
    private int xPercent = 0;
    private int yPercent = 0;
    private int widthPercent = 100;
    private int heightPercent = 100;

    public PercentLayout(Context context) {
        super(context);
    }

    public PercentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PercentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPosition(int xPercent, int yPercent, int widthPercent, int heightPercent) {
        this.xPercent = xPercent;
        this.yPercent = yPercent;
        this.widthPercent = widthPercent;
        this.heightPercent = heightPercent;
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = getDefaultSize(Integer.MAX_VALUE, widthMeasureSpec);
        final int height = getDefaultSize(Integer.MAX_VALUE, heightMeasureSpec);
        setMeasuredDimension(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        final int childWidthMeasureSpec =
                MeasureSpec.makeMeasureSpec(width * widthPercent / 100, MeasureSpec.AT_MOST);
        final int childHeightMeasureSpec =
                MeasureSpec.makeMeasureSpec(height * heightPercent / 100, MeasureSpec.AT_MOST);
        for (int i = 0; i < getChildCount(); ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = right - left;
        final int height = bottom - top;
        // Sub-rectangle specified by percentage values.
        final int subWidth = width * widthPercent / 100;
        final int subHeight = height * heightPercent / 100;
        final int subLeft = left + width * xPercent / 100;
        final int subTop = top + height * yPercent / 100;
        for (int i = 0; i < getChildCount(); ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();
                // Center child both vertically and horizontally.
                final int childLeft = subLeft + (subWidth - childWidth) / 2;
                final int childTop = subTop + (subHeight - childHeight) / 2;
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            }
        }
    }
}
