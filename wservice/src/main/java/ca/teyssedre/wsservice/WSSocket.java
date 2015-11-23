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

package ca.teyssedre.wsservice;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.TrustManager;

import ca.teyssedre.wsservice.contract.ISocketListener;
import ca.teyssedre.wsservice.enums.SocketState;
import ca.teyssedre.wsservice.utils.NoSSLValidation;

public class WSSocket implements AsyncHttpClient.WebSocketConnectCallback,
        WebSocket.StringCallback, DataCallback, CompletedCallback {

    public static final String TAG = "WSSocket";
    public static WSSocket instance;

    //region Properties
    private WebSocket websocket;
    private List<String> queueMsg;
    private List<ISocketListener> listeners;
    private final Object lockQueueMessage;
    private final Object lockListener;
    private Exception exception;
    private SocketState socketState = SocketState.UNKNOWN;
    //endregion

    //region Constructor
    private WSSocket() {
        this.lockQueueMessage = new Object();
        this.lockListener = new Object();
        this.queueMsg = new ArrayList<>();
        this.listeners = new ArrayList<>();
        setSocketState(SocketState.INITIALIZE);
    }

    public static WSSocket getInstance() {
        if (instance == null) {
            instance = new WSSocket();
        }
        return instance;
    }
    //endregion

    /**
     * Function use to establish connection through the socket.
     *
     * @param protocol {@link String} protocol to use {@value "WS"} or {@value "WSS"}.
     * @param host     {@link String} hostname or ip address.
     * @param port     {@link Integer} value of the port.
     * @param path     {@link String} path to add has extension.
     */
    public void Connect(String protocol, String host, int port, String path) {
        setSocketState(SocketState.CONNECTING);
        AsyncHttpClient.getDefaultInstance().websocket(BuildHostString(protocol, host, port, path), null, this);
    }


    /**
     * Help function to build the string connection using parameters.
     *
     * @param protocol {@link String} protocol to use {@value "WS"} or {@value "WSS"}.
     * @param host     {@link String} hostname or ip address.
     * @param port     {@link Integer} value of the port.
     * @param path     {@link String} path to add has extension.
     * @return {@link String} complete string for connection.
     */
    private String BuildHostString(String protocol, String host, int port, String path) {
        StringBuilder b = new StringBuilder();
        if (protocol.contains("wss")) {
            b.append("wss://");
        } else if (protocol.contains("ws")) {
            b.append("ws://");
        }
        if (host != null && host.length() > 0) {
            b.append(host);
        }
        if (port > 0 && (host != null && !host.contains(":"))) {
            b.append(":").append(port);
        }
        if (path != null && path.length() > 0 && (host != null && !host.contains("/"))) {
            if (path.charAt(0) != '/') {
                b.append("/");
            }
            b.append(path);
        }

        return b.toString();
    }

    /**
     * @param enable
     */
    public void EnableSSLValidation(boolean enable) {
        if (!enable) {
            NoSSLValidation sf;
            try {
                sf = new NoSSLValidation("TLSv1.2");
                AsyncHttpClient.getDefaultInstance().getSSLSocketMiddleware().setSSLContext(sf.sslContext);
                AsyncHttpClient.getDefaultInstance().getSSLSocketMiddleware().setHostnameVerifier(sf.hv);
                AsyncHttpClient.getDefaultInstance().getSSLSocketMiddleware().setTrustManagers(new TrustManager[]{sf.tm});
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                setSocketState(SocketState.ERROR);
                this.exception = e;
            }
        }
    }

    /**
     *
     */
    public void Disconnect() {
        if (isSocketConnected()) {
            this.websocket.close();
        }
    }

    /**
     * @param message
     */
    public void Send(String message) {
        if (isSocketConnected()) {
            this.websocket.send(message);
        } else {
            AddToQueue(message);
        }
    }

    /**
     * @param listener
     */
    public void AddListener(ISocketListener listener) {
        synchronized (lockListener) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    /**
     * @param listener
     */
    public void RemoveListener(ISocketListener listener) {
        synchronized (lockListener) {
            Iterator<ISocketListener> iterator = listeners.iterator();
            while (iterator.hasNext()) {
                ISocketListener list = iterator.next();
                if (list == listener) {
                    iterator.remove();
                }
            }
        }
    }

    //region AsyncHttpClient.WebSocketConnectCallback Implementation

    /**
     * @param ex
     * @param webSocket
     */
    @Override
    public void onCompleted(Exception ex, WebSocket webSocket) {
        if (ex != null) {
            setSocketState(SocketState.FAILED);
            this.exception = ex;
            this.websocket = null;
        } else {
            setSocketState(SocketState.CONNECTED);
            this.websocket = webSocket;
            ProcessQueueMessage();
        }
    }
    //endregion

    //region CompletedCallback Implementation

    /**
     * @param ex
     */
    @Override
    public void onCompleted(Exception ex) {
        setSocketState(ex != null ? SocketState.ERROR : SocketState.DISCONNECTED);
        setException(ex);
        this.websocket = null;
    }
    //endregion

    //region DataCallback Implementation

    /**
     * @param emitter
     * @param bb
     */
    @Override
    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {

    }
    //endregion

    //region WebSocket.StringCallback Implementation

    /**
     * @param message
     */
    @Override
    public void onStringAvailable(String message) {
        PushMessage(message);
    }

    //endregion
    public List<ISocketListener> getListeners() {
        return listeners;
    }

    public void setException(Exception exception) {
        this.exception = exception;
        if (this.socketState == SocketState.ERROR) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PushError();
                }
            });
        }
    }
    //region Private Helper

    /**
     * Helper to add message to the queue. The queue is locked to add new message to it to
     * avoid {@link java.util.ConcurrentModificationException}.
     *
     * @param message {@link String} instance to store in the queue.
     */
    private void AddToQueue(String message) {
        synchronized (lockQueueMessage) {
            queueMsg.add(message);
        }
    }

    /**
     * Helper to go over the {@code queueMsg} to push all message queued before the opening of the socket.
     * During the process the queue is locked to avoid {@link java.util.ConcurrentModificationException}.
     */
    private void ProcessQueueMessage() {
        if (this.socketState == SocketState.CONNECTED && this.websocket != null) {
            synchronized (lockQueueMessage) {
                Iterator<String> iterator = queueMsg.iterator();
                while (iterator.hasNext()) {
                    String msg = iterator.next();
                    this.websocket.send(msg);
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Helper to propagate the current {@code state} of the socket to all register {@link ISocketListener}.
     */
    private void PushSocketState() {
        synchronized (lockListener) {
            for (ISocketListener listener : listeners) {
                listener.OnNewSocketState(this.socketState);
            }
        }
    }

    /**
     * Helper to propagate the message to all register {@link ISocketListener}. During the process
     * the {@code listeners} list is locked to avoid {@link java.util.ConcurrentModificationException}.
     *
     * @param message {@link String} message
     */
    private void PushMessage(String message) {
        synchronized (lockListener) {
            for (ISocketListener listener : listeners) {
                listener.OnNewMessage(message);
            }
        }
    }

    /**
     * Helper to propagate the the state the of the Websocket. This propagation is done
     * on a new {@link Thread} each time a new state is set.
     *
     * @param socketState {@link SocketState} value to
     */
    private void setSocketState(SocketState socketState) {
        boolean doPush = this.socketState != socketState;
        this.socketState = socketState;
        if (doPush) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PushSocketState();
                }
            }).start();
        }
    }

    /**
     * @return
     */
    protected boolean isSocketConnected() {
        return this.socketState == SocketState.CONNECTED;
    }

    /**
     * Helper to propagate the {@code exception} to all register {@link ISocketListener}. During the process
     * the {@code listeners} list is locked to avoid {@link java.util.ConcurrentModificationException}.
     */
    private void PushError() {
        synchronized (lockListener) {
            for (ISocketListener listener : listeners) {
                listener.OnError(exception);
            }
        }
    }

    public Exception getException() {
        return exception;
    }

    public SocketState getSocketState() {
        return socketState;
    }
    //endregion
}
