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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.UUID;

import ca.teyssedre.paranoya.R;
import ca.teyssedre.paranoya.fragments.OnItemClickHolder;
import ca.teyssedre.paranoya.messaging.data.User;

public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnDragListener {

    private OnItemClickHolder<ContactViewHolder> onItemClickHolder;
    private ImageView avatar;
    private TextView pseudo;
    private TextView message;
    private UUID userid;
    private int index;

    public ContactViewHolder(View itemView) {
        super(itemView);
        this.itemView.setOnClickListener(this);
        this.itemView.setOnLongClickListener(this);
        avatar = (ImageView) itemView.findViewById(R.id.user_avatar);
        pseudo = (TextView) itemView.findViewById(R.id.user_pseudo);
        message = (TextView) itemView.findViewById(R.id.user_message);
        RelativeLayout wrapper = (RelativeLayout) itemView.findViewById(R.id.wrapper);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wrapper.setBackground(itemView.getContext().getDrawable(R.drawable.ripple));
        }
    }

    public void UpdateView(User user) {
        if (user != null) {
            if (user.getAvatarUrl() != null && user.getAvatarUrl().length() > 0) {
                //TODO: Async fetch image
                System.out.println("downloading image");
            }
            if (user.getPseudo() != null) {
                pseudo.setText(user.getPseudo());
            }
            if (user.getMessage() != null) {
                message.setText(user.getMessage());
            }
        }
    }

    public void UpdateView(User user, int position) {
        index = position;
        UpdateView(user);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (onItemClickHolder != null) {
            onItemClickHolder.OnItemClick(itemView, index, this);
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
        return false;
    }

    public void setItemClickListener(OnItemClickHolder<ContactViewHolder> itemClickListener) {
        this.onItemClickHolder = itemClickListener;
    }
}
