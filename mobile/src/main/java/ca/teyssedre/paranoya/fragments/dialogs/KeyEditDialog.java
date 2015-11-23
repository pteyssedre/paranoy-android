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

package ca.teyssedre.paranoya.fragments.dialogs;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.teyssedre.crypto.Crypto;
import ca.teyssedre.crypto.ICryptoCallback;
import ca.teyssedre.crypto.store.models.KeySet;
import ca.teyssedre.crypto.views.UIHelper;
import ca.teyssedre.paranoya.ParanoyaActivity;
import ca.teyssedre.paranoya.R;
import ca.teyssedre.paranoya.fragments.KeysFragment;
import ca.teyssedre.paranoya.utils.FragmentHelper;

public class KeyEditDialog extends DialogFragment implements View.OnClickListener {


    public static final String TAG = "KeyEditDialog";
    private KeySet keySet;

    public KeyEditDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if (root == null) {
            root = inflater.inflate(R.layout.edit_key, container, false);
            TextView edit = (TextView) root.findViewById(R.id.edit_label);
            TextView delete = (TextView) root.findViewById(R.id.delete_label);
            TextView share = (TextView) root.findViewById(R.id.exchange_label);
            edit.setOnClickListener(this);
            delete.setOnClickListener(this);
            share.setOnClickListener(this);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                edit.setBackground(getActivity().getDrawable(R.drawable.ripple));
                delete.setBackground(getActivity().getDrawable(R.drawable.ripple));
                share.setBackground(getActivity().getDrawable(R.drawable.ripple));
            }
        }
        return root;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_label:
                break;
            case R.id.delete_label:
                Crypto.getInstance(getActivity()).DeleteKeyAsync(keySet.getId(), new ICryptoCallback<Boolean>() {
                    @Override
                    public void OnComplete(Boolean data) {
                        if (data) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    FragmentHelper fragmentHelper = new FragmentHelper((ParanoyaActivity) getActivity());
                                    KeysFragment keysFragment = fragmentHelper.GetFragment(KeysFragment.TAG, KeysFragment.class);
                                    if (keysFragment != null) {
                                        keysFragment.FetchKeysFromDB();
                                    }
                                    Snackbar.make(UIHelper.getInstance().retrievedViewOfType(getActivity(), CoordinatorLayout.class), "Key deleted", Snackbar.LENGTH_SHORT).show();
                                    KeyEditDialog.this.dismiss();
                                }
                            });
                        }
                    }
                });
                break;
        }
    }

    public KeySet getKeySet() {
        return keySet;
    }

    public void setKeySet(KeySet keySet) {
        this.keySet = keySet;
    }
}
