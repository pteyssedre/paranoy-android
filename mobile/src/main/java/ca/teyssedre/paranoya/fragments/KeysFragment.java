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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.teyssedre.crypto.Crypto;
import ca.teyssedre.crypto.ICryptoCallback;
import ca.teyssedre.crypto.store.models.KeySet;
import ca.teyssedre.paranoya.R;
import ca.teyssedre.paranoya.adapters.KeySetAdapter;
import ca.teyssedre.paranoya.fragments.dialogs.CreateKeyDialog;
import ca.teyssedre.paranoya.fragments.dialogs.KeyEditDialog;
import ca.teyssedre.paranoya.holders.KeySetViewHolder;

public class KeysFragment extends ParanoyaFragment implements OnItemClickHolder<KeySetViewHolder> {

    public static final String TAG = "KeysFragment";

    private TextView empty;
    private List<KeySet> _keys;
    private KeySetAdapter adapter;


    public KeysFragment() {
        fabAction = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateKeyDialog dialog = new CreateKeyDialog();
                dialog.show(getActivity().getSupportFragmentManager(), CreateKeyDialog.TAG);
//                Crypto.getInstance(getActivity()).AddRSAKeyAsync(2048, new ICryptoCallback<Boolean>() {
//                    @Override
//                    public void OnComplete(Boolean data) {
//                        FetchKeysFromDB();
//                    }
//                });
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _keys = new ArrayList<>();
        adapter = new KeySetAdapter(_keys);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.keys_fragment, container, false);
            RecyclerView list = (RecyclerView) rootView.findViewById(R.id.keys_list);
            empty = (TextView) rootView.findViewById(R.id.keys_empty);

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            list.setLayoutManager(llm);
            list.setAdapter(adapter);

            adapter.setItemClickListener(this);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FetchKeysFromDB();
    }

    public void FetchKeysFromDB() {
        Crypto.getInstance(getActivity()).GetAllStoredKeysAsync(new ICryptoCallback<List<KeySet>>() {
            @Override
            public void OnComplete(final List<KeySet> data) {
                _keys = data;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        empty.setVisibility(_keys != null && _keys.size() > 0 ? View.GONE : View.VISIBLE);
                        adapter.setData(_keys);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v        The view that was clicked.
     * @param position position of the item in the list.
     * @param item     view holder bind to the view.
     */
    @Override
    public void OnItemClick(View v, int position, KeySetViewHolder item) {
        KeyEditDialog editDialog = new KeyEditDialog();
        editDialog.setKeySet(item.getKeySet());
        editDialog.show(getActivity().getSupportFragmentManager(), KeyEditDialog.TAG);
    }

    /**
     * Called when a view has been long clicked.
     *
     * @param v        The view that was clicked.
     * @param position position of the item in the list.
     * @param item     view holder bind to the view.
     */
    @Override
    public boolean OnItemLongClick(View v, int position, KeySetViewHolder item) {
        return false;
    }
}
