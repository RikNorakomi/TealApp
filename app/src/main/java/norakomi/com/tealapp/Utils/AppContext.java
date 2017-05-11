package norakomi.com.tealapp.Utils;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Rik van Velzen, on 30-10-2016.
 */
public class AppContext extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        AppContext.context = getApplicationContext();

        initRealm();
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public static Context getAppContext() {
        return AppContext.context;
    }
}
