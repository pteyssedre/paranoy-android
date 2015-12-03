package ca.teyssedre.paranoya.messaging.data;

import android.os.Parcel;
import android.os.Parcelable;

public class KeyMessage implements Parcelable {

    private String publicKey;
    private String privateKey;
    private boolean system;

    public KeyMessage() {
    }

    protected KeyMessage(Parcel in) {
        publicKey = in.readString();
        privateKey = in.readString();
        system = in.readByte() != 0;
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

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(publicKey);
        dest.writeString(privateKey);
        dest.writeByte((byte) (system ? 1 : 0));
    }
}
