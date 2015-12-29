package ca.teyssedre.paranoya.messaging.data;

import android.os.Parcel;
import android.os.Parcelable;

public class KeyElement implements Parcelable  {

    private String publicKey;
    private String privateKey;
    private int system;

    public KeyElement() {
    }

    protected KeyElement(Parcel in) {
        publicKey = in.readString();
        privateKey = in.readString();
        system = in.readInt();
    }

    public static final Creator<KeyElement> CREATOR = new Creator<KeyElement>() {
        @Override
        public KeyElement createFromParcel(Parcel in) {
            return new KeyElement(in);
        }

        @Override
        public KeyElement[] newArray(int size) {
            return new KeyElement[size];
        }
    };

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public int getSystem() {
        return system;
    }

    public void setSystem(int system) {
        this.system = system;
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
        dest.writeString(publicKey);
        dest.writeString(privateKey);
        dest.writeInt(system);
    }
}
