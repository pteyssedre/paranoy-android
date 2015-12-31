/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015. Pierre Teyssedre
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

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;

import ca.teyssedre.crypto.Crypto;
import ca.teyssedre.paranoya.messaging.SocketMessage;
import ca.teyssedre.paranoya.messaging.data.KeyMessage;
import ca.teyssedre.paranoya.messaging.data.User;
import ca.teyssedre.paranoya.messaging.enums.SocketMessageType;

public class PMessageLogic {

    public static final String TAG = "PMessageLogic";

    private final ObjectMapper mapper;
    private PublicKey serverKey;
    private PrivateKey currentUserKey;

    public PMessageLogic() {
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void OnMessage(String message) {
        SocketMessageType type = SocketMessage.parseType(message);
        switch (type) {
            case KeyExchange:
                SocketMessage<KeyMessage> msg = null;
                try {
                    msg = mapper.readValue(message, new TypeReference<SocketMessage<KeyMessage>>() {
                    });
                    if (msg.getData() != null) {
                        if (msg.getData().isSystem()) {
                            try {
                                serverKey = Crypto.StringToPublicKey(msg.getData().getKey().getPublicKey());
                                Log.d(TAG, "Server key found ");
                                validateDataWithKey(message, serverKey);
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //TODO: New user/contact key exchange.
                            if (msg.getOrigin() == null || msg.getOrigin().length() == 0) {
                                // invalid information on user ... no id found
                                return;
                            }
                            msg.getOrigin();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case OnlineStatus:
                SocketMessage<User> parsed = SocketMessage.parse(message);
                Log.d(TAG, "New user status " + parsed.getData());
                break;

            case KeyValidation:
                //TODO: Prompt
                SocketMessage.parse(message);
                break;
            case DataText:
                break;
            default:
                Log.e(TAG, "Message type not recognized");
                break;
        }
    }

    private void validateDataWithKey(String message, PublicKey serverKey) {
        try {
            JSONObject parsed = new JSONObject(message);
            if (parsed.has("data") && parsed.has("signature")) {
                String data = parsed.getJSONObject("data").toString();
                String signature = parsed.getString("signature");

//                byte[] pubBytes = serverKey.getEncoded();
//                SubjectPublicKeyInfo spkInfo = SubjectPublicKeyInfo.getInstance(pubBytes);
//                ASN1Primitive primitive = spkInfo.parsePublicKey();
//                byte[] publicKeyPKCS1 = primitive.getEncoded();

                boolean isValid = Crypto.ValidateSignatureWithRSA(serverKey, data.getBytes("UTF-8"), signature.getBytes("UTF-8"));
//                byte[] bytes = Crypto.DecryptWithRSA(serverKey, Crypto.base64Decode(signature));
//                String str = new String(bytes, StandardCharsets.UTF_8);
//                Log.d(TAG, " Trying :" +str);
                if (!isValid) {
                    Log.e(TAG, "message can't be validated");
                    return;
                }
                Log.d(TAG, "message validated");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentUserKey(PrivateKey currentUserKey) {
        this.currentUserKey = currentUserKey;
    }
}
