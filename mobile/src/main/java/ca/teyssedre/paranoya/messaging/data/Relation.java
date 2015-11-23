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
package ca.teyssedre.paranoya.messaging.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Relation implements Parcelable {

    private long Id = -1;
    private long UserId = -1;
    private long KeyId = -1;
    private int Type = 0;
    private String description = "";

    public Relation() {
    }

    public Relation(long id, long userId, long keyId, int type, String description) {
        Id = id;
        UserId = userId;
        KeyId = keyId;
        Type = type;
        this.description = description;
    }

    protected Relation(Parcel in) {
        Id = in.readLong();
        UserId = in.readLong();
        KeyId = in.readLong();
        Type = in.readInt();
        description = in.readString();
    }

    public static final Creator<Relation> CREATOR = new Creator<Relation>() {
        @Override
        public Relation createFromParcel(Parcel in) {
            return new Relation(in);
        }

        @Override
        public Relation[] newArray(int size) {
            return new Relation[size];
        }
    };

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }

    public long getKeyId() {
        return KeyId;
    }

    public void setKeyId(long keyId) {
        KeyId = keyId;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(Id);
        dest.writeLong(UserId);
        dest.writeLong(KeyId);
        dest.writeInt(Type);
        dest.writeString(description);
    }
}
