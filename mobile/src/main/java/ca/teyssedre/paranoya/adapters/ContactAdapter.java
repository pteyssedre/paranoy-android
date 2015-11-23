/**
 * ********************************************************************************************************
 * <p/>
 * All rights reserved © 2015  -  Innovative Imaging Technologies  -  www.iitreacts.com
 * This computer program may not be used, copied, distributed, corrected, modified, translated,
 * transmitted or assigned without Innovative Imaging Technologies's prior written authorization.
 * <p/>
 * Created by  :  pteyssedre
 * Date        :  15-11-02
 * Application :  Paranoya .
 * Package     :  ca.teyssedre.paranoya.adapters .
 * <p/>
 * ********************************************************************************************************
 */
package ca.teyssedre.paranoya.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import ca.teyssedre.paranoya.R;
import ca.teyssedre.paranoya.fragments.OnItemClickHolder;
import ca.teyssedre.paranoya.holders.ContactViewHolder;
import ca.teyssedre.paranoya.messaging.data.User;

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {


    private User[] _data;
    private OnItemClickHolder<ContactViewHolder> itemClickListener;

    public ContactAdapter() {
    }

    public ContactAdapter(User[] data, OnItemClickHolder<ContactViewHolder> itemClickListener) {
        this._data = data;
        this.itemClickListener = itemClickListener;
    }

    public ContactAdapter(User[] data) {
        this._data = data;
    }

    public void setData(User[] data) {
        this._data = data;
    }

    public void setData(List<User> data) {
        if (data != null) {
            this._data = new User[data.size()];
            data.toArray(this._data);
        }
    }

    /**
     * Called when RecyclerView needs a new {@link RecyclerView.ViewHolder} of the given type to represent
     * an item.
     * <p/>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using
     * . Since it will be re-used to display
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
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card, parent, false);
        ContactViewHolder viewHolder = new ContactViewHolder(view);
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
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        User item = getItem(position);
        if (item != null) {
            holder.UpdateView(item, position);
        }
    }

    private User getItem(int position) {
        if (_data != null && _data.length > 0 && position > -1 && position < _data.length) {
            return _data[position];
        }
        return null;
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return _data != null ? _data.length : 0;
    }

    public void setItemClickListener(OnItemClickHolder<ContactViewHolder> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
