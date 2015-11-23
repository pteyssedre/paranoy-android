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

import android.os.Binder;

import ca.teyssedre.wsservice.contract.ISocketListener;
import ca.teyssedre.wsservice.contract.IWebSocketService;
import ca.teyssedre.wsservice.enums.SocketState;

public class WebSocketBinder extends Binder {

    private final WebSocketService websocket;

    /**
     * Constructor for the {@link Binder} to exchange with the service.
     *
     * @param webSocketService {@link WebSocketService} instance to link through the {@link Binder}.
     */
    public WebSocketBinder(WebSocketService webSocketService) {
        this.websocket = webSocketService;
    }

    /**
     * Sending the message through the socket.
     *
     * @param s {@link String} instance representing the serialized message to send.
     */
    public void Send(String s) {
        websocket.Send(s);
    }

    //<editor-fold desc="Use SSL Connection">

    /**
     * Shorter function to create a secure connection using <code>wss</code> protocol and <code>443</code> port
     * not {@code path} will be used and only the {@code host} will be set as parameter.
     *
     * @param host {@link String} hostname or ip address to connect to using WSS protocol.
     */
    public void SecureConnect(String host) {
        websocket.Connect("wss", host, 443, null);
    }

    /**
     * Shorter function to create a secure connection using <code>wss</code> protocol
     * not {@code path} will be used and only the {@code host} and the {@code port} will be set as parameter.
     *
     * @param host {@link String} hostname or ip address to connect to using WSS protocol.
     * @param port {@link Integer} value of the port to use.
     */
    public void SecureConnect(String host, int port) {
        websocket.Connect("wss", host, port, null);
    }

    /**
     * Shorter to create a secure connection using <code>wss</code> protocol.
     *
     * @param host {@link String} hostname or ip address to connect to using WSS protocol.
     * @param port {@link Integer} value of the port to use.
     * @param path {@link String} path value to add to target a specific service.
     */
    public void SecureConnect(String host, int port, String path) {
        websocket.Connect("wss", host, port, path);
    }
    //</editor-fold>

    //<editor-fold desc="Use Plain-Text Connection">

    /**
     * In order to connect to a specific socket server, the {@link IWebSocketService} expose the desire {@code protocol},
     * {@code hostname}, {@code port} and {@code path}.
     *
     * @param host {@link String} hostname or ip address.
     * @param port int value for the desire port.
     * @param path {@link String} additional path to add at the url.
     */
    public void Connect(String host, int port, String path) {
        websocket.Connect("ws", host, port, path);
    }

    /**
     * In order to connect to a specific socket server, the {@link IWebSocketService} expose the desire {@code protocol},
     * {@code hostname}, {@code port} and {@code path}.
     *
     * @param protocol {@link String} value could be {@code "WS"} or {@code "WSS"}
     * @param host     {@link String} hostname or ip address.
     * @param port     int value for the desire port.
     * @param path     {@link String} additional path to add at the url.
     */
    public void Connect(String protocol, String host, int port, String path) {
        websocket.Connect(protocol, host, port, path);
    }
    //</editor-fold>

    /**
     * Every {@code listener} which wants to receive notification from the {@link ca.teyssedre.wsservice.WebSocketService}
     * service must be register through this method.
     *
     * @param listener {@link ISocketListener} instance to register.
     */
    public void AddListener(ISocketListener listener) {
        websocket.AddListener(listener);
    }

    /**
     * When a {@code listener} doesn't want to receive notification it can be remove from the
     * {@link ca.teyssedre.wsservice.WebSocketService} listeners list.
     *
     * @param listener {@link ISocketListener} instance to remove.
     */
    public void RemoveListener(ISocketListener listener) {
        websocket.RemoveListener(listener);
    }

    /**
     * Disconnect the socket and reset the {@link ca.teyssedre.wsservice.enums.SocketState} of the
     * {@link ca.teyssedre.wsservice.WebSocketService}.
     */
    public void Disconnect() {
        websocket.Disconnect();
    }

    /**
     * In order to diagnose {@link ca.teyssedre.wsservice.enums.SocketState#FAILED} or {@link ca.teyssedre.wsservice.enums.SocketState#ERROR}
     * the {@link ca.teyssedre.wsservice.WebSocketService} expose an exception property.
     *
     * @return {@link Exception} catch if one has been rise by the socket.
     */
    public Exception getException() {
        return websocket.getException();
    }

    /**
     * Getter of the {@link SocketState} of the {@link ca.teyssedre.wsservice.WebSocketService}.
     *
     * @return {@link SocketState} current value.
     */
    public SocketState getSocketState() {
        return websocket.getSocketState();
    }
}
