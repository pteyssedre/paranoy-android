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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;

import ca.teyssedre.wsservice.contract.ISocketListener;
import ca.teyssedre.wsservice.contract.IWebSocketService;
import ca.teyssedre.wsservice.enums.SocketState;

public class WebSocketService extends Service implements IWebSocketService {

    private WSSocket socket;

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p/>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (socket == null) {
            socket = WSSocket.getInstance();
        }
        return new WebSocketBinder(this);
    }

    /**
     * Every {@code listener} which wants to receive notification from the {@link ca.teyssedre.wsservice.WebSocketService}
     * service must be register through this method.
     *
     * @param listener {@link ISocketListener} instance to register.
     */
    @Override
    public void AddListener(ISocketListener listener) {
        socket.AddListener(listener);
    }

    /**
     * In order to connect to a specific socket server, the {@link IWebSocketService} expose the desire {@code protocol},
     * {@code hostname}, {@code port} and {@code path}.
     *
     * @param protocol {@link String} value could be {@code "WS"} or {@code "WSS"}
     * @param hostname {@link String} hostname or ip address.
     * @param port     int value for the desire port.
     * @param path     {@link String} additional path to add at the url.
     */
    @Override
    public void Connect(String protocol, String hostname, int port, String path) {
        socket.Connect(protocol, hostname, port, path);
    }

    /**
     * When the {@link ca.teyssedre.wsservice.WebSocketService} recieved a serialized message
     * it will try send it if the socket is open. Otherwise the message will be queued and process
     * when the connection will be open.
     *
     * @param message {@link String} serialized message to send.
     */
    @Override
    public void Send(String message) {
        socket.Send(message);
    }

    /**
     * Disconnect the socket and reset the {@link ca.teyssedre.wsservice.enums.SocketState} of the
     * {@link ca.teyssedre.wsservice.WebSocketService}.
     */
    @Override
    public void Disconnect() {
        socket.Disconnect();
    }

    /**
     * When a {@code listener} doesn't want to receive notification it can be remove from the
     * {@link ca.teyssedre.wsservice.WebSocketService} listeners list.
     *
     * @param listener {@link ISocketListener} instance to remove.
     */
    @Override
    public void RemoveListener(ISocketListener listener) {
        socket.RemoveListener(listener);
    }

    /**
     * Shorter to disable SSL validation on the connection through WSS protocol. This feature is for
     * development use only. It must be call before the {@link IWebSocketService#Connect(String, String, int, String)} method.
     *
     * @param enable flag value to disable
     */
    @Deprecated
    @Override
    public void DisableSSLValidation(boolean enable) {
        socket.EnableSSLValidation(enable);
    }

    /**
     * In order to diagnose {@link ca.teyssedre.wsservice.enums.SocketState#FAILED} or {@link ca.teyssedre.wsservice.enums.SocketState#ERROR}
     * the {@link ca.teyssedre.wsservice.WebSocketService} expose an exception property.
     *
     * @return {@link Exception} catch if one has been rise by the socket.
     */
    @Override
    public Exception getException() {
        return socket.getException();
    }

    /**
     * Getter of the {@link SocketState} of the {@link ca.teyssedre.wsservice.WebSocketService}.
     *
     * @return {@link SocketState} current value.
     */
    @Override
    public SocketState getSocketState(){
        return socket.getSocketState();
    }
}
