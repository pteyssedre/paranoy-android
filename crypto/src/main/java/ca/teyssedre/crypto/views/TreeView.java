/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Pierre Teyssedre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ca.teyssedre.crypto.views;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class TreeView {

    private final View view;
    private final View[] children;
    private List<TreeView> treeViews;

    public TreeView(View view) {
        this.view = view;
        this.children = UIHelper.retrieveChildrenViews(view);
        this.treeViews = new ArrayList<>();
    }

    private TreeView getTreeViewOf(View view) {
        TreeView v = null;
        if (view != null) {
            v = new TreeView(view);
        }
        return v;
    }

    public List<TreeView> getTreeViews() {
        if (treeViews.size() == 0 && children.length > 0) {
            for (View child : children) {
                treeViews.add(new TreeView(child));
            }
        }
        return treeViews;
    }

    public View[] getChildren() {
        return children;
    }

    public View getView() {
        return view;
    }
}