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

import android.util.Log;

import java.util.List;

import ca.teyssedre.crypto.Crypto;
import ca.teyssedre.crypto.store.models.KeySet;
import ca.teyssedre.paranoya.messaging.data.User;
import ca.teyssedre.paranoya.store.sources.ParanoyaUserSource;

public class IdentityHelper {

    public static final String TAG = "IdentityHelper";
    private final Crypto crypto;
    private final ParanoyaUserSource userSource;

    User currentUser;
    List<User> Contacts;

    public IdentityHelper(Crypto crypto, ParanoyaUserSource userSource) {
        this.crypto = crypto;
        this.userSource = userSource;
    }

    public User getCurrentUser() {
        if (currentUser == null) {
            List<User> usersByType = userSource.getUsersByType(1);
            if (usersByType.size() > 0) {
                if (usersByType.size() > 1) {
                    Log.e(TAG, "More than one main user type 1");
                    //TODO: show popup for multi account support
                } else {
                    currentUser = usersByType.get(0);
                }
            } else {
                //TODO: no user
            }
        }
        return currentUser;
    }

    public List<User> getMyContacts() {
        if (getCurrentUser() != null) {
            userSource.getContactsList(getCurrentUser().getId());
        }
        return null;
    }

    public KeySet getIdentityKey() {
        if (getCurrentUser() != null) {
            List<Long> ids = userSource.getKeysByUserId(getCurrentUser().getId());
            long keyId = ids.get(0);
            return crypto.GetStoredKey(keyId);
        }
        return null;
    }

}
