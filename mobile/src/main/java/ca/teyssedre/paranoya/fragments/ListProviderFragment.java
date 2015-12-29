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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.teyssedre.crypto.Crypto;
import ca.teyssedre.paranoya.R;
import ca.teyssedre.paranoya.adapters.StringRecycleAdapter;
import ca.teyssedre.paranoya.holders.SingleStringViewHolder;

public class ListProviderFragment extends ParanoyaFragment implements OnItemClickHolder<SingleStringViewHolder>, View.OnClickListener {

    private static final String TAG = "ListProvider";
    private StringRecycleAdapter adapter;
    private RecyclerView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if (root == null) {
            root = inflater.inflate(R.layout.provider_fragment, container, false);
            list = (RecyclerView) root.findViewById(R.id.provider_list);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            list.setLayoutManager(llm);
            list.setOnClickListener(this);
            adapter = new StringRecycleAdapter();
            adapter.setItemClickListener(this);
            adapter.setData(Crypto.GetProvidersNames());
            list.setAdapter(adapter);
            if (adapter.getItemCount() > 0) {
//                root.findViewById(R.id.provider_empty).setVisibility(View.GONE);
            }
        }
        return root;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v        The view that was clicked.
     * @param position position of the item in the list.
     * @param item     view holder bind to the view.
     */
    @Override
    public void OnItemClick(View v, int position, SingleStringViewHolder item) {
        Log.d(TAG, "Click on " + adapter.getItem(position));
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
    public boolean OnItemLongClick(View v, int position, SingleStringViewHolder item) {
        return false;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Log.d(TAG, "Testing " + v.toString());
    }
}
