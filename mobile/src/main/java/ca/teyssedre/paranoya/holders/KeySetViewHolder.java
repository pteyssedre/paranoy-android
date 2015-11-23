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
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.teyssedre.crypto.store.models.KeySet;
import ca.teyssedre.paranoya.R;
import ca.teyssedre.paranoya.fragments.OnItemClickHolder;

public class KeySetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnDragListener {

    private final TextView title;
    private final TextView description;
    private final TextView info;
    private final TextView created;
    private int index;
    private OnItemClickHolder<KeySetViewHolder> itemClickListener;
    private KeySet keySet;

    public KeySetViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.item_title);
        description = (TextView) itemView.findViewById(R.id.item_description);
        info = (TextView) itemView.findViewById(R.id.item_info);
        created = (TextView) itemView.findViewById(R.id.item_created);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        itemView.setOnDragListener(this);

        LinearLayout wrapper = (LinearLayout) itemView.findViewById(R.id.wrapper);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wrapper.setBackground(itemView.getContext().getDrawable(R.drawable.ripple));
        }
    }

    public void updateView(KeySet keySet) {
        title.setText(keySet.getTitle());
        description.setText(keySet.getDescription());
        created.setText(keySet.getCreated().toString());
        String infoStr = itemView.getContext().getString(R.string.invalid_key_set);
        if (keySet.getPrivateKey() != null) {
            infoStr = keySet.getPrivateKey().getAlgorithm() + " " + keySet.getPrivateKey().getFormat() + " " + keySet.getLength();
        } else if (keySet.getSecretKey() != null) {
            infoStr = keySet.getSecretKey().getAlgorithm() + " " + keySet.getSecretKey().getFormat() + " " + keySet.getLength();
        }
        info.setText(infoStr);
        this.keySet = keySet;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public KeySet getKeySet() {
        return keySet;
    }

    public void setItemClickListener(OnItemClickHolder<KeySetViewHolder> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            itemClickListener.OnItemClick(v, index, this);
        }

    }

    /**
     * Called when a view has been clicked and held.
     *
     * @param v The view that was clicked and held.
     * @return true if the callback consumed the long click, false otherwise.
     */
    @Override
    public boolean onLongClick(View v) {
        if (itemClickListener != null) {
            itemClickListener.OnItemLongClick(v, index, this);
        }
        return false;
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
}
