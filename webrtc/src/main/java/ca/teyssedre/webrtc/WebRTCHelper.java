/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2015 Pierre Teyssedre
 * Created by  :  pteyssedre
 * Date        :  15-11-18
 * Application :  Paranoya .
 * Package     :  ca.teyssedre.webrtc .
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ca.teyssedre.webrtc;

import android.content.Context;
import android.util.Log;

import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.EglBase;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebRTCHelper {

    private final Context context;
    private final EglBase elgBase;
    private final PeerConnectionFactory factory;
    private boolean loopback = false;

    private static final String TAG = "WebRTCHelper";
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";
    private static final String FIELD_TRIAL_AUTOMATIC_RESIZE = "WebRTC-MediaCodecVideoEncoder-AutomaticResize/Enabled/";
    private static final String VIDEO_CODEC_VP8 = "VP8";
    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String VIDEO_CODEC_H264 = "H264";
    private static final String AUDIO_CODEC_OPUS = "opus";
    private static final String AUDIO_CODEC_ISAC = "ISAC";
    private static final String VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate";
    private static final String AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate";
    private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
    private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";
    private static final String MAX_VIDEO_WIDTH_CONSTRAINT = "maxWidth";
    private static final String MIN_VIDEO_WIDTH_CONSTRAINT = "minWidth";
    private static final String MAX_VIDEO_HEIGHT_CONSTRAINT = "maxHeight";
    private static final String MIN_VIDEO_HEIGHT_CONSTRAINT = "minHeight";
    private static final String MAX_VIDEO_FPS_CONSTRAINT = "maxFrameRate";
    private static final String MIN_VIDEO_FPS_CONSTRAINT = "minFrameRate";
    private static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";
    private static final int HD_VIDEO_WIDTH = 1280;
    private static final int HD_VIDEO_HEIGHT = 720;
    private static final int MAX_VIDEO_WIDTH = 1280;
    private static final int MAX_VIDEO_HEIGHT = 1280;
    private static final int MAX_VIDEO_FPS = 30;
    private static WebRTCHelper instance;

    private VideoCapturerAndroid localCameraCaptured;
    private boolean noAudioProcessing;

    private WebRTCHelper(Context context) {
        this.context = context;
        PeerConnectionFactory.initializeAndroidGlobals(context, true, true, false);
        this.factory = new PeerConnectionFactory();
        this.elgBase = new EglBase();
    }

    public static WebRTCHelper getInstance(Context context) {
        if (instance == null) {
            instance = new WebRTCHelper(context);
        }
        return instance;
    }

    //<editor-fold desc="Capturer">

    /**
     * Shorter to capture Front camera using the {@link CameraEnumerationAndroid}.
     *
     * @return {@link VideoCapturerAndroid} instance.
     */
    public VideoCapturerAndroid RequestFrontCamera() {
        int numberOfCameras = CameraEnumerationAndroid.getDeviceCount();
        String cameraDeviceName = CameraEnumerationAndroid.getDeviceName(0);
        String frontCameraDeviceName = CameraEnumerationAndroid.getNameOfFrontFacingDevice();
        if (numberOfCameras > 1 && frontCameraDeviceName != null) {
            cameraDeviceName = frontCameraDeviceName;
        }
        Log.d(TAG, "Opening camera: " + cameraDeviceName);
        VideoCapturerAndroid videoCapturer = VideoCapturerAndroid.create(cameraDeviceName, null);
        if (videoCapturer == null) {
            Log.e(TAG, "ERROR video capturer null");
        }
        return videoCapturer;
    }

    /**
     * Shorter to capture Back camera using the {@link CameraEnumerationAndroid}.
     *
     * @return {@link VideoCapturerAndroid} instance.
     */
    public VideoCapturerAndroid RequestBackCamera() {
        int numberOfCameras = CameraEnumerationAndroid.getDeviceCount();
        String cameraDeviceName = CameraEnumerationAndroid.getDeviceName(0);
        String backCamera = CameraEnumerationAndroid.getNameOfBackFacingDevice();
        if (numberOfCameras > 1 && backCamera != null) {
            cameraDeviceName = backCamera;
        }
        Log.d(TAG, "Opening camera: " + cameraDeviceName);
        VideoCapturerAndroid videoCapturer = VideoCapturerAndroid.create(cameraDeviceName, null);
        if (videoCapturer == null) {
            Log.e(TAG, "ERROR video capturer null");
        }
        return videoCapturer;
    }

    /**
     * Shorter to toggle between the front and back camera using the {@code localCameraCaptured} instance.
     *
     * @param handler {@link VideoCapturerAndroid.CameraSwitchHandler} callback.
     */
    public void ToggleCamera(VideoCapturerAndroid.CameraSwitchHandler handler) {
        if (localCameraCaptured == null) {
            localCameraCaptured = RequestBackCamera();
        }
        if (handler == null) {
            handler = new VideoCapturerAndroid.CameraSwitchHandler() {
                @Override
                public void onCameraSwitchDone(boolean b) {
                    Log.d(TAG, "onCameraSwitchDone : " + b);
                }

                @Override
                public void onCameraSwitchError(String s) {
                    Log.e(TAG, "onCameraSwitchError : " + s);
                }
            };
        }
        localCameraCaptured.switchCamera(handler);
    }
    //</editor-fold>

    public SurfaceWebRTC RenderLocalCamera(SurfaceWebRTC target) {
        if (target == null) {
            target = new SurfaceWebRTC(this.context);
        }
        target.init(elgBase.getContext(), null);
        if (localCameraCaptured != null) {
            localCameraCaptured.dispose();
        }
        localCameraCaptured = RequestFrontCamera();
        target.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
        VideoSource videoSource = factory.createVideoSource(localCameraCaptured, RequestVideoConstraints());
        VideoTrack localVideoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        localVideoTrack.setEnabled(true);
        localVideoTrack.addRenderer(new VideoRenderer(target));
        return target;
    }

    //<editor-fold desc="Constraints">

    /**
     * Shorter to set the flags {@code MIN_VIDEO_WIDTH_CONSTRAINT}, {@code MAX_VIDEO_WIDTH_CONSTRAINT},
     * {@code MIN_VIDEO_HEIGHT_CONSTRAINT} and {@code MAX_VIDEO_HEIGHT_CONSTRAINT}
     *
     * @return {@link MediaConstraints} instance with flags set.
     */
    private MediaConstraints RequestVideoConstraints() {
        MediaConstraints videoConstraints = new MediaConstraints();
        int videoWidth = 640;
        int videoHeight = 480;
        videoWidth = Math.min(videoWidth, MAX_VIDEO_WIDTH);
        videoHeight = Math.min(videoHeight, MAX_VIDEO_HEIGHT);
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MIN_VIDEO_WIDTH_CONSTRAINT, Integer.toString(videoWidth)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MAX_VIDEO_WIDTH_CONSTRAINT, Integer.toString(videoWidth)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MIN_VIDEO_HEIGHT_CONSTRAINT, Integer.toString(videoHeight)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MAX_VIDEO_HEIGHT_CONSTRAINT, Integer.toString(videoHeight)));
        return videoConstraints;
    }

    /**
     * @return {@link MediaConstraints} instance containing the {@code DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT} to true of false.
     */
    private MediaConstraints RequestPeerConnectionConstraints() {
        MediaConstraints pcConstraints = new MediaConstraints();
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair(DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, String.valueOf(!loopback)));
        return pcConstraints;
    }

    private MediaConstraints RequestAudioConstraints() {
        MediaConstraints audioConstraints = new MediaConstraints();

        if (noAudioProcessing) {
            Log.d(TAG, "Disabling audio processing");
        }
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, String.valueOf(noAudioProcessing)));
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, String.valueOf(noAudioProcessing)));
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, String.valueOf(noAudioProcessing)));
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, String.valueOf(noAudioProcessing)));

        return audioConstraints;
    }

    private MediaConstraints RequestSDPContstraints(boolean hasAudio, boolean hasVideo) {
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", String.valueOf(hasAudio)));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", String.valueOf(hasVideo)));
        return sdpMediaConstraints;
    }
    //</editor-fold>

    public void setEnableLoopback(boolean enable) {
        loopback = enable;
    }

    //<editor-fold desc="SDP Helper">

    /**
     * Helper to force the use of a codec.
     *
     * @param sdpDescription {@link org.webrtc.SessionDescription} as {@link String} value.
     * @param codec          {@link String} Codec to force.
     * @param isAudio        {@link Boolean} flag to identify audio
     * @return {@link String} value representing the {@link org.webrtc.SessionDescription} to set.
     */
    private static String preferCodec(String sdpDescription, String codec, boolean isAudio) {
        String[] lines = sdpDescription.split("\r\n");
        int mLineIndex = -1;
        String codecRtpMap = null;
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
        Pattern codecPattern = Pattern.compile(regex);
        String mediaDescription = "m=video ";
        if (isAudio) {
            mediaDescription = "m=audio ";
        }
        for (int i = 0; (i < lines.length)
                && (mLineIndex == -1 || codecRtpMap == null); i++) {
            if (lines[i].startsWith(mediaDescription)) {
                mLineIndex = i;
                continue;
            }
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1);
                continue;
            }
        }
        if (mLineIndex == -1) {
            Log.w(TAG, "No " + mediaDescription + " line, so can't prefer " + codec);
            return sdpDescription;
        }
        if (codecRtpMap == null) {
            Log.w(TAG, "No rtpmap for " + codec);
            return sdpDescription;
        }
        Log.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap + ", prefer at "
                + lines[mLineIndex]);
        String[] origMLineParts = lines[mLineIndex].split(" ");
        if (origMLineParts.length > 3) {
            StringBuilder newMLine = new StringBuilder();
            int origPartIndex = 0;
            // Format is: m=<media> <port> <proto> <fmt> ...
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(codecRtpMap);
            for (; origPartIndex < origMLineParts.length; origPartIndex++) {
                if (!origMLineParts[origPartIndex].equals(codecRtpMap)) {
                    newMLine.append(" ").append(origMLineParts[origPartIndex]);
                }
            }
            lines[mLineIndex] = newMLine.toString();
            Log.d(TAG, "Change media description: " + lines[mLineIndex]);
        } else {
            Log.e(TAG, "Wrong SDP media description format: " + lines[mLineIndex]);
        }
        StringBuilder newSdpDescription = new StringBuilder();
        for (String line : lines) {
            newSdpDescription.append(line).append("\r\n");
        }
        return newSdpDescription.toString();
    }
    //</editor-fold>

    public void Dispose() {
        if (localCameraCaptured != null) {
            localCameraCaptured.dispose();
        }
    }
}
