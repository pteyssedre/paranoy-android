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

package ca.teyssedre.paranoya.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ca.teyssedre.paranoya.R;
import ca.teyssedre.paranoya.adapters.ContactAdapter;
import ca.teyssedre.paranoya.holders.ContactViewHolder;
import ca.teyssedre.paranoya.messaging.data.User;
import ca.teyssedre.paranoya.store.sources.ParanoyaUserSource;

public class ContactFragment extends ParanoyaFragment implements OnItemClickHolder<ContactViewHolder> {

    public static final String TAG = "ContactFragment";

    private static final String CONTACTS = "CONTACTS";

    private RecyclerView list;
    private ContactAdapter adapter;
    private ArrayList<User> contacts;

    public ContactFragment() {
        contacts = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        if (v == null) {
            v = inflater.inflate(R.layout.contact_fragment, container, false);
            list = (RecyclerView) v.findViewById(R.id.contact_list);
            adapter = new ContactAdapter();
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            list.setLayoutManager(llm);
            list.setAdapter(adapter);
            adapter.setItemClickListener(this);
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tryToRestoreView(savedInstanceState);
        List<User> allUsers = ParanoyaUserSource.getInstance().getAllUsers();
        adapter.setData(allUsers);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (contacts != null && contacts.size() > 0) {
            outState.putParcelableArrayList(CONTACTS, contacts);
        }

    }

    private void tryToRestoreView(Bundle bundle) {
        if (bundle != null) {
            contacts = bundle.getParcelableArrayList(CONTACTS);
            if (adapter == null) {
                adapter = new ContactAdapter();
            }
            adapter.setData(contacts);
            if (list != null) {
                list.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
            //TODO: load contact from db ...
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v        The view that was clicked.
     * @param position position of the item in the list.
     * @param item     view holder bind to the view.
     */
    @Override
    public void OnItemClick(View v, int position, ContactViewHolder item) {
        // TODO: On contact click send friend request + exchange keys if needed
    }

    /**
     * Called when a view has been long clicked.
     *
     * @param v        The view that was clicked.
     * @param position position of the item in the list.
     * @param item     view holder bind to the view.
     * @return boolean
     */
    @Override
    public boolean OnItemLongClick(View v, int position, ContactViewHolder item) {
        return false;
    }
}
