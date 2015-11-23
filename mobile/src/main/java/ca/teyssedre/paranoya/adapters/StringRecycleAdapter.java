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

package ca.teyssedre.paranoya.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;
import java.util.Set;

import ca.teyssedre.paranoya.R;
import ca.teyssedre.paranoya.fragments.OnItemClickHolder;
import ca.teyssedre.paranoya.holders.SingleStringViewHolder;

public class StringRecycleAdapter extends RecyclerView.Adapter<SingleStringViewHolder> {


    private String[] data;
    private OnItemClickHolder<SingleStringViewHolder> itemClickListener;

    public StringRecycleAdapter() {
    }

    /**
     * Called when RecyclerView needs a new {@link RecyclerView.ViewHolder} of the given type to represent
     * an item.
     * <p/>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     */
    @Override
    public SingleStringViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_item_view, parent, false);
        SingleStringViewHolder viewHolder = new SingleStringViewHolder(view);
        if (itemClickListener != null) {
            viewHolder.setItemClickListener(itemClickListener);
        }
        return viewHolder;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link RecyclerView.ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p/>
     * Override instead if Adapter can
     * handle effcient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(SingleStringViewHolder holder, int position) {
        String data = getItem(position);
        holder.setIndex(position);
        holder.UpdateData(data);
    }

    /**
     * Getter to retrieve {@link String} value from the {@code data} array. If the position given
     * is not valid a null value will be return.
     *
     * @param position index value to fetch.
     * @return {@link String} value or null if {@code position} is invalid.
     */
    public String getItem(int position) {
        if (position >= 0 && this.data != null && this.data.length > position) {
            return data[position];
        }
        return null;
    }

    /**
     * Setter
     *
     * @param data {@link String[]}.
     */
    public void setData(String[] data) {
        this.data = data;
    }

    /**
     * @param data {@link List<String>} instance
     */
    public void setData(List<String> data) {
        if (data != null) {
            this.data = new String[data.size()];
            data.toArray(this.data);
        }
    }

    /**
     * @param data {@link Set<String>}
     */
    public void setData(Set<String> data) {
        if (data != null) {
            this.data = new String[data.size()];
            data.toArray(this.data);
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return data != null ? data.length : 0;
    }

    public void setItemClickListener(OnItemClickHolder<SingleStringViewHolder> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
