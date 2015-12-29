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

package ca.teyssedre.crypto.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;

public class UIHelper {

    private Context context;
    private DialogInterface.OnCancelListener onCancelListener;
    private ProgressDialog progressDialog;

    //region Singleton
    private static UIHelper instance;

    private UIHelper(Context context) {
        this.context = context;
        this.onCancelListener = new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        };
    }

    public static UIHelper getInstance(Context context) {
        if (context == null) {
            throw new RuntimeException("Null Context pass to UIHelper");
        }
        if (instance == null) {
            instance = new UIHelper(context);
        }else{
            instance.setContext(context);
        }
        return instance;
    }

    public static UIHelper getInstance() {
        if (instance == null) {
            throw new RuntimeException("UIHelper instance not instantiated. You must call getInstance with a valid Context.");
        }
        return instance;
    }
    //endregion

    public void showProgressDialog(String text) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(context, null, text, true, false, onCancelListener);
        }
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public <T extends View> T retrievedViewOfType(Activity activity, Class<T> type) {
        if (activity != null) {
            View root = activity.getWindow().getDecorView();
            return lookFor(root, type);
            //not found
        }
        return null;
    }

    public <T extends View> T lookFor(View view, Class<T> type) {
        T t = null;
        View[] children = retrieveChildrenViews(view);
        for (View v : children) {
            if (v.getClass() == type) {
                return type.cast(v);
            }
        }
        for (View v : children) {
            t = lookFor(v, type);
            if (t != null) {
                break;
            }
        }
        return t;
    }

    public static View[] retrieveChildrenViews(View view) {
        View[] results = new View[0];
        ViewGroup viewGroup1;
        try {
            viewGroup1 = (ViewGroup) view;
        } catch (ClassCastException e) {
            return results;
        }
        int childCount = viewGroup1.getChildCount();
        results = new View[childCount];
        for (int j = 0; j < childCount; j++) {
            results[j] = viewGroup1.getChildAt(j);
        }
        return results;
    }

    public TreeView getTreeViewOf(View v) {
        return new TreeView(v);
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
