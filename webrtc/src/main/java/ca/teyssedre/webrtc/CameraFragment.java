/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Pierre Teyssedre
 * Created by  :  pteyssedre
 * Date        :  15-11-18
 * Application :  Paranoya .
 * Package     :  ca.teyssedre.paranoya.fragments .
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
package ca.teyssedre.webrtc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class CameraFragment extends Fragment {

    public static final String TAG = "CameraFragment";
    private RelativeLayout root;
    private SurfaceWebRTC streamView;
    private PercentLayout layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        if (v == null) {
            v = inflater.inflate(R.layout.camera_layout, container, false);
            root = (RelativeLayout) v.findViewById(R.id.root_view);
            layout = (PercentLayout) v.findViewById(R.id.percentWrapper);
            streamView = (SurfaceWebRTC) v.findViewById(R.id.streamView);
            streamView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebRTCHelper.getInstance(getActivity()).ToggleCamera(null);
                }
            });
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (root != null) {
            WebRTCHelper.getInstance(getActivity()).RenderLocalCamera(streamView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        WebRTCHelper.getInstance(getActivity()).Dispose();
    }

    public void setPosition(int xPercent, int yPercent, int withPercent, int heightPercent) {
        if (layout != null)
            layout.setPosition(xPercent, yPercent, withPercent, heightPercent);
    }
}
