/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2015 Pierre Teyssedre
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

package ca.teyssedre.paranoya.messaging.enums;

/**
 * Enum to identify the type of action to take when {@link ca.teyssedre.paranoya.messaging.SocketMessage}
 * should be process by the client.
 */
public enum SocketMessageType {

    Unknown(-1),
    KeyExchange(1),
    OnlineStatus(100),
    KeyValidation(200),
    DataText(300),
    DataBinary(400),
    CleanProcess(500),
    WipeOut(600);


    private final int value;

    SocketMessageType(int code) {
        this.value = code;
    }

    public int getValue() {
        return value;
    }

    public static SocketMessageType parse(int code) {
        switch (code) {
            case 1:
                return KeyExchange;
            case 100:
                return OnlineStatus;
            case 200:
                return KeyValidation;
            case 300:
                return DataText;
            case 400:
                return DataBinary;
            case 500:
                return CleanProcess;
            case 600:
                return WipeOut;
            default:
                return Unknown;
        }
    }
}
