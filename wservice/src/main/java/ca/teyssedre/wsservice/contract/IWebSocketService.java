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

package ca.teyssedre.wsservice.contract;

import ca.teyssedre.wsservice.enums.SocketState;
import ca.teyssedre.wsservice.socket.WebSocketService;

public interface IWebSocketService {


    /**
     * Every {@code listener} which wants to receive notification from the {@link WebSocketService}
     * service must be register through this method.
     *
     * @param listener {@link ISocketListener} instance to register.
     */
    void AddListener(ISocketListener listener);

    /**
     * Shorter to disable SSL validation on the connection through WSS protocol. This feature is for
     * development use only. It must be call before the {@link IWebSocketService#Connect(String, String, int, String)} method.
     *
     * @param enable flag value to disable
     */
    @Deprecated
    void DisableSSLValidation(boolean enable);

    /**
     * In order to connect to a specific socket server, the {@link IWebSocketService} expose the desire {@code protocol},
     * {@code hostname}, {@code port} and {@code path}.
     *
     * @param protocol {@link String} value could be {@code "WS"} or {@code "WSS"}
     * @param hostname {@link String} hostname or ip address.
     * @param port     int value for the desire port.
     * @param path     {@link String} additional path to add at the url.
     */
    void Connect(String protocol, String hostname, int port, String path);

    /**
     * When the {@link WebSocketService} recieved a serialized message
     * it will try send it if the socket is open. Otherwise the message will be queued and process
     * when the connection will be open.
     *
     * @param message {@link String} serialized message to send.
     */
    void Send(String message);

    /**
     * Disconnect the socket and reset the {@link ca.teyssedre.wsservice.enums.SocketState} of the
     * {@link WebSocketService}.
     */
    void Disconnect();

    /**
     * When a {@code listener} doesn't want to receive notification it can be remove from the
     * {@link WebSocketService} listeners list.
     *
     * @param listener {@link ISocketListener} instance to remove.
     */
    void RemoveListener(ISocketListener listener);


    /**
     * In order to diagnose {@link ca.teyssedre.wsservice.enums.SocketState#FAILED} or {@link ca.teyssedre.wsservice.enums.SocketState#ERROR}
     * the {@link WebSocketService} expose an exception property.
     *
     * @return {@link Exception} catch if one has been rise by the socket.
     */
    Exception getException();

    /**
     * Getter of the {@link SocketState} of the {@link WebSocketService}.
     *
     * @return {@link SocketState} current value.
     */
    SocketState getSocketState();
}
