package norakomi.com.tealapp.Utils;

import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Rik van Velzen, Norakomi.com, on 5-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class App {

    private final static String LOG_PREFIX = "Norakomi: ";

    public static void log(String classTag, String message) {
        Log.v(LOG_PREFIX + classTag, message);
    }

    public static void logError(String classTag, String message) {
        Log.e(LOG_PREFIX + classTag, message);
    }

    public static void logError(String classTag, String message, Throwable t) {
        Log.e(LOG_PREFIX + classTag, message);
    }

    public static void toast(String string) {
        Toast toast = Toast.makeText(AppContext.getAppContext(), string, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    public static void toastLong(String string) {
        Toast toast = Toast.makeText(AppContext.getAppContext(), string, Toast.LENGTH_LONG);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }
}
