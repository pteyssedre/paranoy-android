/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Pierre Teyssedre
 * Created by  :  pteyssedre
 * Date        :  15-11-20
 * Application :  Paranoya .
 * Package     :  ca.teyssedre.paranoya.utils .
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

import android.util.Log;

import java.util.List;

import ca.teyssedre.paranoya.messaging.data.User;
import ca.teyssedre.paranoya.store.sources.ParanoyaUserSource;

public class IdentityHelper {

    public static final String TAG = "IdentityHelper";

    static User currentUser;
    List<String> Contacts;

    public static User getCurrentUser() {
        if (currentUser == null) {
            List<User> usersByType = ParanoyaUserSource.getInstance().getUsersByType(1);
            if (usersByType.size() > 0) {
                if (usersByType.size() > 1) {
                    Log.e(TAG, "More than one main user type 1");
                } else {
                    currentUser = usersByType.get(0);
                }
            }
        }
        return currentUser;
    }


}
