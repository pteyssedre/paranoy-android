/**
 * ********************************************************************************************************
 * <p/>
 * All rights reserved © 2015  -  Innovative Imaging Technologies  -  www.iitreacts.com
 * This computer program may not be used, copied, distributed, corrected, modified, translated,
 * transmitted or assigned without Innovative Imaging Technologies's prior written authorization.
 * <p/>
 * Created by  :  pteyssedre
 * Date        :  15-11-14
 * Application :  Paranoya .
 * Package     :  ca.teyssedre.paranoya.fragments .
 * <p/>
 * ********************************************************************************************************
 */
package ca.teyssedre.paranoya.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IdentityFragment extends ParanoyaFragment {

    public static final String TAG = "IdentityFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        if (v == null) {

        }
        return v;
    }
}
