/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Pierre Teyssedre
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

package ca.teyssedre.paranoya.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

import ca.teyssedre.paranoya.R;

public class PrimaryButton extends Button {

    public PrimaryButton(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBackground(context.getResources().getDrawable(R.drawable.ripple_primary, context.getTheme()));
        } else {
            setBackground(context.getResources().getDrawable(R.drawable.white_border));
        }
        setTextColor(context.getResources().getColor(R.color.white));
    }

    public PrimaryButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBackground(context.getResources().getDrawable(R.drawable.ripple_primary, context.getTheme()));
        } else {
            setBackground(context.getResources().getDrawable(R.drawable.white_border));
        }
        setTextColor(context.getResources().getColor(R.color.white));
    }

    public PrimaryButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBackground(context.getResources().getDrawable(R.drawable.ripple_primary, context.getTheme()));
        } else {
            setBackground(context.getResources().getDrawable(R.drawable.white_border));
        }
        setTextColor(context.getResources().getColor(R.color.white));
    }
}