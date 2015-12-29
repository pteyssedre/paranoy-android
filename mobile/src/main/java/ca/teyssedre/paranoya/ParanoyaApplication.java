package ca.teyssedre.paranoya;

import android.app.Activity;
import android.app.Application;

import java.util.Map;
import java.util.Set;

import ca.teyssedre.crypto.Crypto;
import ca.teyssedre.crypto.store.models.CryptoInfo;
import ca.teyssedre.paranoya.store.sources.ParanoyaUserSource;
import ca.teyssedre.paranoya.utils.SocketClient;

public class ParanoyaApplication extends Application {

    private static final String TAG = "Paranoya";

    SocketClient socketManager;
    Crypto crypto;
    ParanoyaUserSource userSource;
    Activity currentActivity;
    CryptoInfo cryptoInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        socketManager = new SocketClient(this);
        crypto = Crypto.getInstance(this);
        userSource = ParanoyaUserSource.getInstance(this);
        userSource.initialization();
        cryptoInfo = new CryptoInfo();
    }

    public void Connect() {
        Map<String, Map<String, Set<String>>> tryme = cryptoInfo.getTryme();
    }

    public void setCurrentActivity(Activity activity) {
        this.currentActivity = activity;
        crypto.UpdateActivity(this.currentActivity);
        socketManager.UpdateActivity(this.currentActivity);
    }
}
