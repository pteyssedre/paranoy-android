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

public interface ISocketListener {

    /**
     * The service {@link ca.teyssedre.wsservice.WebSocketService} will notify the current listener
     * by this method.
     *
     * @param state {@link SocketState} value.
     */
    void OnNewSocketState(SocketState state);

    /**
     * On every message recieved by the {@link ca.teyssedre.wsservice.WebSocketService} the current
     * listener will be notify through this method.
     *
     * @param message {@link String} message serialized.
     */
    void OnNewMessage(String message);

    /**
     * When an error is raise in the {@link ca.teyssedre.wsservice.WebSocketService} the exception is
     * push back through this function.
     *
     * @param exception {@link Exception} instance to provide information about the error raised.
     */
    void OnError(Exception exception);

    /**
     * Function to clean all variables to force the garbage collection.
     * The {@code Dispose} will trigger a {@code Disconnect}.
     */
    void Dispose();

    /**
     * Function use to disconnect the socket without cleaning variables such as {@link SocketState}
     * {@link Exception}.
     */
    void Disconnect();

    /**
     * In order to make the {@link ca.teyssedre.wsservice.WSSocket} available to any {@link android.app.Activity},
     * the socket is expose through a service {@link ca.teyssedre.wsservice.WebSocketService}. This
     * function will explicitly bound a {@link android.app.Activity} to the service.
     */
    void boundToService();

    /**
     * To prevent multi binding issue and proper dispose of variables, this function will unbound a
     * {@link android.app.Activity} to the {@link ca.teyssedre.wsservice.WebSocketService}.
     */
    void unboundToService();

    /**
     * In case of {@link SocketState#FAILED} the listener can request the {@code exception} for
     * diagnosis purpose.
     *
     * @return {@link Exception} instance if one was catch.
     */
    Exception getException();

    /**
     * Explicit Connect to the default service.
     */
    void Connect();
}
