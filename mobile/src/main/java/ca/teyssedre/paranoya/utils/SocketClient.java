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

package ca.teyssedre.paranoya.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.util.Random;

import ca.teyssedre.crypto.views.UIHelper;
import ca.teyssedre.paranoya.messaging.SocketMessage;
import ca.teyssedre.paranoya.messaging.data.User;
import ca.teyssedre.paranoya.messaging.enums.SocketMessageType;
import ca.teyssedre.wsservice.WebSocketBinder;
import ca.teyssedre.wsservice.WebSocketService;
import ca.teyssedre.wsservice.contract.ISocketListener;
import ca.teyssedre.wsservice.enums.SocketState;

public class SocketClient implements ISocketListener {

    public static final String TAG = "SocketClient";
    //region Properties
    private Activity activity;
    private WebSocketBinder binder;
    private Snackbar snackbar;
    private int msgSerial;
    private boolean _needConnect = false;
    private boolean _connectCalled = false;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (WebSocketBinder) service;
            binder.AddListener(SocketClient.this);
            if (_needConnect) {
                Connect();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };
    //endregion

    //region Constructor
    public SocketClient(Activity activity) {
        this.activity = activity;
        this.msgSerial = new Random().nextInt();
    }
    //endregion

    //region Bound & Unbound to Service

    /**
     * In order to make the {@link ca.teyssedre.wsservice.WSSocket} available to any {@link android.app.Activity},
     * the socket is expose through a service {@link ca.teyssedre.wsservice.WebSocketService}. This
     * function will explicitly bound a {@link android.app.Activity} to the service.
     */
    @Override
    public void boundToService() {
        Intent intent = new Intent(this.activity, WebSocketService.class);
        this.activity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * To prevent multi binding issue and proper dispose of variables, this function will unbound a
     * {@link android.app.Activity} to the {@link ca.teyssedre.wsservice.WebSocketService}.
     */
    @Override
    public void unboundToService() {
        if (this.activity != null) {
            this.activity.unbindService(serviceConnection);
        }
    }
    //endregion

    //region OnNewState

    /**
     * The service {@link ca.teyssedre.wsservice.WebSocketService} will notify the current listener
     * by this method.
     *
     * @param state {@link SocketState} value.
     */
    @Override
    public void OnNewSocketState(SocketState state) {
        switch (state) {
            case INITIALIZE:
                Snack("Initializing");
                break;
            case CONNECTING:
                Snack("Connecting");
                break;
            case CONNECTED:
                Snack("Connected", 2000);
                // TODO: Send online status to server
                Send(NewMessage(SocketMessageType.OnlineStatus, IdentityHelper.getCurrentUser(), ""));
                break;
            case FAILED:
                Snack("Fail to connect ...");
                Exception exception = getException();
                if (exception != null) {
                    exception.printStackTrace();
                }
                break;
            default:
                Snack(state.toString(), 4000);
                break;
        }
    }
    //endregion

    //region OnMessage

    /**
     * On every message recieved by the {@link ca.teyssedre.wsservice.WebSocketService} the current
     * listener will be notify through this method.
     *
     * @param message {@link String} message serialized.
     */
    @Override
    public void OnNewMessage(String message) {
        SocketMessageType type = SocketMessage.parseType(message);
        switch (type) {
            case OnlineStatus:
                //TODO: Push to contact list
                SocketMessage<User> parsed = SocketMessage.parse(message, User.class);
                System.out.println(parsed);
                break;
            case KeyExchange:
                //TODO: Prompt Friend Request Dialog
                break;
            case KeyValidation:
                //TODO: Prompt
                break;
            case DataText:
                break;
        }
    }
    //endregion

    //region Connect & Disconnect

    /**
     * Explicit Connect to the default service.
     */
    @Override
    public void Connect() {
        if (binder != null) {
            if (binder.getSocketState() == SocketState.INITIALIZE && !_connectCalled) {
                //JSON REST SOAP Whatever ... Database maybe ...
                binder.Connect("teyssedre.ca", 4445, null);
                _needConnect = false;
                _connectCalled = true;
            }
        } else {
            _needConnect = true;
        }
    }

    /**
     * Function use to disconnect the socket without cleaning variables such as {@link SocketState}
     * {@link Exception}.
     */
    @Override
    public void Disconnect() {
        if (this.binder != null) {
            this.binder.Disconnect();
            this.binder = null;
        }
    }
    //endregion

    public void Send(SocketMessage message) {
        if (binder != null && binder.getSocketState() == SocketState.CONNECTED) {
            Log.d(TAG, "Outgoing message :" + message.toString());
            binder.Send(message.toString());
        } else {
            //TODO: Queue messages ?
        }
    }

    //region OnError

    /**
     * In case of {@link SocketState#FAILED} the listener can request the {@code exception} for
     * diagnosis purpose.
     *
     * @return {@link Exception} instance if one was catch.
     */
    @Override
    public Exception getException() {
        return this.binder.getException();
    }

    /**
     * When an error is raise in the {@link ca.teyssedre.wsservice.WebSocketService} the exception is
     * push back through this function.
     *
     * @param exception {@link Exception} instance to provide information about the error raised.
     */
    @Override
    public void OnError(Exception exception) {
        Snack("Error happen");
    }
    //endregion

    public <T> SocketMessage<T> NewMessage(SocketMessageType type, T data, String destination) {
        SocketMessage<T> message = new SocketMessage<>(type, data, destination);
        message.setSerial(msgSerial);
        message.setOrigin(""); //TODO: set socket id ... ?
        return message;
    }

    //region UI Helpers

    /**
     * Shorter to execute {@code runnable} inside the UI Thread of the current {@code activity}.
     *
     * @param runnable {@link Runnable} to execute inside the UI Thread.
     */
    private void RunOnUI(Runnable runnable) {
        if (this.activity != null) {
            this.activity.runOnUiThread(runnable);
        }
    }

    /**
     * Helper to retrieved the {@link CoordinatorLayout} of the current {@code activity} instance.
     *
     * @return {@link View} that should be the instance of type {@link CoordinatorLayout}.
     */
    private View getRootView() {
        if (this.activity != null) {
            UIHelper uiHelper = UIHelper.getInstance(this.activity);
            return uiHelper.retrievedViewOfType(this.activity, CoordinatorLayout.class);
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
     * all the interaction with it is done in the UI Thread if the {@code activity}.
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(delay);
                        RunOnUI(new Runnable() {
                            @Override
                            public void run() {
                                if (snackbar != null) {
                                    snackbar.dismiss();
                                    snackbar = null;
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    //endregion

    /**
     * Function to clean all variables to force the garbage collection.
     * The {@code Dispose} will trigger a {@code Disconnect}.
     */
    @Override
    public void Dispose() {
        if (this.binder != null) {
            this.binder.RemoveListener(SocketClient.this);
            Disconnect();
        }
        this.activity = null;
    }

}
