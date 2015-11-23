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

package ca.teyssedre.paranoya.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.json.JSONException;
import org.json.JSONObject;

import ca.teyssedre.paranoya.messaging.enums.SocketMessageType;

public class SocketMessage<T> {

    private SocketMessageType type;
    private T data;
    private String destination;
    private String origin;
    private int serial;
    private String signature;

    public SocketMessage(SocketMessageType type, T data, String destination) {
        this.type = type;
        this.data = data;
        this.destination = destination;
    }

    public SocketMessage(SocketMessageType type, T data, String destination, String origin, int serial) {
        this.type = type;
        this.data = data;
        this.destination = destination;
        this.origin = origin;
        this.serial = serial;
    }

    public SocketMessageType getType() {
        return type;
    }

    public void setType(SocketMessageType type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    private String Serialize() {
        JSONObject jsonObject = new JSONObject();
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            JSONObject d = new JSONObject(mapper.writeValueAsString(data));
            jsonObject.put("type", type.getValue());
            jsonObject.put("destination", destination);
            jsonObject.put("data", d);
            jsonObject.put("origin", origin);
            jsonObject.put("serial", serial);
        } catch (JSONException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public String toString() {
        return Serialize();
    }

    public static SocketMessageType parseType(String raw) {
        if (raw != null && raw.length() > 0) {
            try {
                return SocketMessageType.parse(new JSONObject(raw).getInt("type"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return SocketMessageType.Unknown;
    }

    public static <E> SocketMessage<E> parse(String raw, Class<E> type) {
        SocketMessage<E> msg = null;
        try {
            JSONObject json = new JSONObject(raw);
            String destination = json.getString("destination");
            String origin = json.getString("origin");
            int serial = json.getInt("serial");
            int typ = json.getInt("type");
            Object data = json.get("data");
            msg = new SocketMessage<>(SocketMessageType.parse(typ), type.cast(data), destination, origin, serial);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msg;
    }
}
