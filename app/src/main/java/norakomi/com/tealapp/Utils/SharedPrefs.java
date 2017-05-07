package norakomi.com.tealapp.Utils;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Rik van Velzen, Norakomi.com, on 7-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class SharedPrefs {

    private static final String APP_SHARED_PREFS = "norakomi_preferences"; //  Name of the file -.xml
    private static final String AUTOPLAY_ENABLED = "autoplayEnabled";

    private static final SharedPrefs instance = new SharedPrefs();

    private final SharedPreferences sharedPrefs;
    private final SharedPreferences.Editor prefsEditor;

    public static SharedPrefs getInstance() {
        return instance;
    }

    private SharedPrefs() {
        sharedPrefs = AppContext.getAppContext().getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        prefsEditor = sharedPrefs.edit();
    }

    public boolean isAutoplayEnabled(){
        return sharedPrefs.getBoolean(AUTOPLAY_ENABLED, false);
    }

    public void setAutoplayEnabled(boolean enabled){
        prefsEditor.putBoolean(AUTOPLAY_ENABLED, enabled);
        prefsEditor.apply();
    }

}
