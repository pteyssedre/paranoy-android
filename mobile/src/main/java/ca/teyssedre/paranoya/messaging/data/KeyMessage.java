package ca.teyssedre.paranoya.messaging.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class KeyMessage implements Parcelable {

    private KeyElement Key;
    private String id;
    private String[] connected;

    public KeyMessage() {
    }

    protected KeyMessage(Parcel in) {
        Key = in.readParcelable(KeyElement.class.getClassLoader());
        id = in.readString();
        connected = in.createStringArray();
    }

    public static final Creator<KeyMessage> CREATOR = new Creator<KeyMessage>() {
        @Override
        public KeyMessage createFromParcel(Parcel in) {
            return new KeyMessage(in);
        }

        @Override
        public KeyMessage[] newArray(int size) {
            return new KeyMessage[size];
        }
    };

    public KeyElement getKey() {
        return Key;
    }

    public void setKey(KeyElement key) {
        Key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getConnected() {
        return connected;
    }

    public void setConnected(String[] connected) {
        this.connected = connected;
    }

    @JsonIgnore
    public boolean isSystem() {
        return Key != null && Key.getSystem() == 1;
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
        dest.writeParcelable(Key, flags);
        dest.writeString(id);
        dest.writeStringArray(connected);
    }
}
