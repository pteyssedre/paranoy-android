/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015. Pierre Teyssedre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ca.teyssedre.paranoya.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.security.PublicKey;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ca.teyssedre.crypto.store.models.CryptoInfo;
import ca.teyssedre.crypto.views.UIHelper;
import ca.teyssedre.paranoya.messaging.SocketMessage;
import ca.teyssedre.paranoya.messaging.enums.SocketMessageType;
import ca.teyssedre.wsservice.contract.ISocketListener;
import ca.teyssedre.wsservice.enums.SocketState;
import ca.teyssedre.wsservice.socket.WSSocket;
import ca.teyssedre.wsservice.socket.WebSocketService;

public class SocketClient implements ISocketListener {

    public static final String TAG = "SocketClient";

    private final Handler uiThread;
    private final ThreadPoolExecutor background;
    private final CryptoInfo cryptoInfo;
    private IParanoyaMessageListener listner;

    //region Properties
    private Context context;
    private WSSocket socket;
    private Snackbar snackbar;
    private int msgSerial;
    private boolean _connectCalled = false;
    private PublicKey serverKey;
    private PMessageLogic PMLogic;
    private IdentityHelper idHelper;
    //endregion

    //region Constructor
    public SocketClient(Context context) {
        this.context = context;
        this.msgSerial = new Random().nextInt();
        this.uiThread = new Handler(Looper.getMainLooper());
        this.socket = new WSSocket();
        this.socket.AddListener(this);
        int i = Runtime.getRuntime().availableProcessors();
        LinkedBlockingDeque<Runnable> queue = new LinkedBlockingDeque<>();
        this.background = new ThreadPoolExecutor(1, i, 1, TimeUnit.SECONDS, queue);
        this.PMLogic = new PMessageLogic();
        this.cryptoInfo = new CryptoInfo();
    }
    //endregion

    //region OnNewState

    /**
     * The service {@link WebSocketService} will notify the current listener
     * by this method.
     *
     * @param state {@link SocketState} value.
     */
    @Override
    public void OnNewSocketState(final SocketState state) {
        background.execute(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case INITIALIZE:
                        Snack("Initializing");
                        break;
                    case CONNECTING:
                        Snack("Connecting");
                        break;
                    case CONNECTED:
                        Snack("Connected", 2000);
                        // Send identity
                        Send(NewMessage(SocketMessageType.OnlineStatus, idHelper.getCurrentUser()));
                        break;
                    case FAILED:
                        Exception exception = getException();
                        if (exception != null && exception.getMessage() != null) {
                            Log.e(TAG, exception.getMessage());
                            Snack(exception.getMessage(), 4000);
                        } else {
                            Snack("Fail to connect ...", 4000);
                        }
                        break;
                    default:
                        Snack(state.toString(), 4000);
                        break;
                }
            }
        });
    }
    //endregion

    //region OnMessage

    /**
     * On every message received by the {@link WebSocketService} the current
     * listener will be notify through this method.
     *
     * @param message {@link String} message serialized.
     */
    @Override
    public void OnNewMessage(final String message) {
        Log.d(TAG, "Incoming message :" + message);
        background.execute(new Runnable() {
            @Override
            public void run() {
                PMLogic.OnMessage(message);
            }
        });
    }
    //endregion

    //region Connect & Disconnect

    /**
     * Explicit Connect to the default service.
     */
    @Override
    public void Connect() {
        if (socket != null) {
            background.execute(new Runnable() {
                @Override
                public void run() {
                    if (socket.getSocketState() == SocketState.INITIALIZE && !_connectCalled) {
                        socket.SecureConnect("teyssedre.ca", 4445);
                        _connectCalled = true;
                    }
                }
            });
        }
    }

    /**
     * Function use to disconnect the socket without cleaning variables such as {@link SocketState}
     * {@link Exception}.
     */
    @Override
    public void Disconnect() {
        if (this.socket != null) {
            this.socket.Disconnect();
            this.socket = null;
        }
    }
    //endregion

    //region OnError

    /**
     * In case of {@link SocketState#FAILED} the listener can request the {@code exception} for
     * diagnosis purpose.
     *
     * @return {@link Exception} instance if one was catch.
     */
    @Override
    public Exception getException() {
        return this.socket.getException();
    }

    /**
     * When an error is raise in the {@link WebSocketService} the exception is
     * push back through this function.
     *
     * @param exception {@link Exception} instance to provide information about the error raised.
     */
    @Override
    public void OnError(Exception exception) {
        Snack("Error happen");
    }
    //endregion

    //region Send Message
    public void Send(final SocketMessage message) {
        background.execute(new Runnable() {
            @Override
            public void run() {
                if (socket != null && socket.getSocketState() == SocketState.CONNECTED) {
                    Log.d(TAG, "Outgoing message :" + message.toString());
                    socket.Send(message.toString());
                } else {
                    Log.e(TAG, "Trying to send message " + message.toString());
                    //TODO: Queue messages ?
                }
            }
        });
    }

    public <T> SocketMessage<T> NewMessage(SocketMessageType type, T data) {
        SocketMessage<T> message = new SocketMessage<>(type, data, null);
        message.setSerial(msgSerial);
        message.setOrigin(""); //TODO: set socket id ... ?
        msgSerial++;
        return message;
    }

    public <T> SocketMessage<T> NewMessage(SocketMessageType type, T data, String destination) {
        SocketMessage<T> message = new SocketMessage<>(type, data, destination);
        message.setSerial(msgSerial);
        message.setOrigin(""); //TODO: set socket id ... ?
        msgSerial++;
        return message;
    }
    //endregion

    //region UI Helpers

    /**
     * Shorter to execute {@code runnable} inside the UI Thread of the current {@code context}.
     *
     * @param runnable {@link Runnable} to execute inside the UI Thread.
     */
    private void RunOnUI(Runnable runnable) {
        if (this.context != null) {
            this.uiThread.post(runnable);
        }
    }

    /**
     * Helper to retrieved the {@link CoordinatorLayout} of the current {@code context} instance.
     *
     * @return {@link View} that should be the instance of type {@link CoordinatorLayout}.
     */
    private View getRootView() {
        if (this.context != null) {
            try {
                UIHelper uiHelper = UIHelper.getInstance(this.context);
                return uiHelper.retrievedViewOfType((Activity) this.context, CoordinatorLayout.class);
            } catch (ClassCastException ex) {
                Log.e(TAG, "Can't cast context into activity");
            }
        }
        return null;
    }

    /**
     * Shorter to display and hide the the {@code snackbar}.
     *
     * @param text  {@link String} message to display on the {@code snackbar}.
     * @param delay delay before dismiss {@code snackbar}
     */
    private void Snack(String text, int delay) {
        Snack(text);
        EatSnack(delay);
    }

    /**
     * Helper to display the {@code snackbar}. If it's already displayed only the text will be changed.
     * The {@code snackbar} is created if needed and set to {@link Snackbar#LENGTH_INDEFINITE}.
     * To ensure the proper display of the {@code snackbar}, the {@link #getRootView()} method is use and
     * all the interaction with it is done in the UI Thread if the {@code context}.
     *
     * @param text {@link String} text to display
     */
    private void Snack(final String text) {
        if (getRootView() != null) {
            RunOnUI(new Runnable() {
                @Override
                public void run() {
                    if (snackbar == null) {
                        snackbar = Snackbar.make(getRootView(), text, Snackbar.LENGTH_INDEFINITE);
                    }
                    snackbar.setText(text);
                    if (!snackbar.isShown()) {
                        snackbar.show();
                    }
                }
            });
        }
    }

    /**
     * Shorter to launch a delayed {@link Thread} then call the UI Thread to execute the {@link Snackbar#dismiss()} method.
     *
     * @param delay number of milliseconds to sleep before calling the UI Thread.
     */
    private void EatSnack(final int delay) {
        if (getRootView() != null) {
            if (snackbar != null) {
                snackbar.getView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        snackbar.dismiss();
                        snackbar = null;
                    }
                }, delay);
            }
        }
    }
    //endregion

    /**
     * Function to clean all variables to force the garbage collection.
     * The {@code Dispose} will trigger a {@code Disconnect}.
     */
    @Override
    public void Dispose() {
        if (this.socket != null) {
            this.socket.RemoveListener(SocketClient.this);
            Disconnect();
        }
        this.context = null;
    }

    public void UpdateActivity(Activity activity) {
        this.context = activity;
    }

    public void setIdHelper(IdentityHelper idHelper) {
        this.idHelper = idHelper;
        if (this.idHelper != null) {
            if (this.idHelper.getCurrentUser() != null) {
                PMLogic.setCurrentUserKeySet(this.idHelper.getIdentityKey());
            }
        }
    }
}
