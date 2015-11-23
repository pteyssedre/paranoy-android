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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

import ca.teyssedre.crypto.Crypto;
import ca.teyssedre.crypto.ICryptoCallback;
import ca.teyssedre.crypto.store.models.KeySet;
import ca.teyssedre.paranoya.ParanoyaActivity;
import ca.teyssedre.paranoya.R;
import ca.teyssedre.paranoya.messaging.data.Relation;
import ca.teyssedre.paranoya.messaging.data.User;
import ca.teyssedre.paranoya.store.sources.ParanoyaUserSource;

public class CreateUserFragment extends ParanoyaFragment {

    public static final String TAG = "CreateUserFragment";
    private EditText pseudo;
    private EditText message;
    private Button generate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        if (v == null) {
            v = inflater.inflate(R.layout.create_user, container, false);
            pseudo = (EditText) v.findViewById(R.id.user_pseudo);
            message = (EditText) v.findViewById(R.id.user_message);
            generate = (Button) v.findViewById(R.id.generate_user);
            addActions();
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
        }
        return v;
    }

    private void addActions() {
        pseudo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                System.out.println(event + ", " + actionId);
                return false;
            }
        });
        message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                System.out.println(event + ", " + actionId);
                return false;
            }
        });
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                KeySet s = new KeySet();
                s.setTitle(pseudo.getText().toString() + "'s identity");
                s.setDescription(message.getText().toString());
                s.setLength(2048);
                Crypto.getInstance(getActivity()).PushRSAKeyAsync(s, new ICryptoCallback<KeySet>() {
                    @Override
                    public void OnComplete(KeySet data) {
                        if (data != null) {
                            try {
                                ParanoyaUserSource instance = ParanoyaUserSource.getInstance(getActivity());

                                User u = new User();
                                u.setPseudo(pseudo.getText().toString());
                                u.setMessage(message.getText().toString());
                                u.setHash(Crypto.PublicKeyRSAToString(data.getPublicKey()));
                                u.setType(1);
                                u = instance.addUser(u);

                                Relation relationKey = new Relation();
                                relationKey.setUserId(u.getId());
                                relationKey.setKeyId(data.getId());
                                relationKey.setType(1);
                                instance.addRelation(relationKey);

                                ((ParanoyaActivity) getActivity()).OnIdentityCreated();
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }
}
