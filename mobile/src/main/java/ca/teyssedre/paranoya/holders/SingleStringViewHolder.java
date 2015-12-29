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

package ca.teyssedre.paranoya.holders;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ca.teyssedre.paranoya.R;
import ca.teyssedre.paranoya.fragments.OnItemClickHolder;

public class SingleStringViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnDragListener {

    private OnItemClickHolder<SingleStringViewHolder> itemClickListener;
    private TextView textView;
    private int index;

    public SingleStringViewHolder(View itemView) {
        super(itemView);
        this.itemView.setOnClickListener(this);
        this.itemView.setOnLongClickListener(this);
        textView = (TextView) itemView.findViewById(R.id.item_title);
        RelativeLayout wrapper = (RelativeLayout) itemView.findViewById(R.id.ripple_wrapper);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wrapper.setBackground(itemView.getContext().getDrawable(R.drawable.ripple));
        }
    }

    public void UpdateData(String data) {
        if (this.textView != null) {
            textView.setText(data);
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (this.itemClickListener != null) {
            this.itemClickListener.OnItemClick(v, index, this);
        }
    }

    /**
     * Called when a drag event is dispatched to a view. This allows listeners
     * to get a chance to override base View behavior.
     *
     * @param v     The View that received the drag event.
     * @param event The {@link DragEvent} object for the drag event.
     * @return {@code true} if the drag event was handled successfully, or {@code false}
     * if the drag event was not handled. Note that {@code false} will trigger the View
     * to call its handler.
     */
    @Override
    public boolean onDrag(View v, DragEvent event) {
        return false;
    }

    /**
     * Called when a view has been clicked and held.
     *
     * @param v The view that was clicked and held.
     * @return true if the callback consumed the long click, false otherwise.
     */
    @Override
    public boolean onLongClick(View v) {
        return this.itemClickListener != null && this.itemClickListener.OnItemLongClick(v, index, this);
    }

    public void setItemClickListener(OnItemClickHolder<SingleStringViewHolder> itemClickListener) {
        this.itemClickListener = itemClickListener;
        if (this.itemClickListener != null && itemView != null) {
            itemView.setOnClickListener(this);
        }
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
