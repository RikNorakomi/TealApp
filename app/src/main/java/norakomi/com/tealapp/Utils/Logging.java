package norakomi.com.tealapp.Utils;

import android.util.Log;

/**
 * Created by Rik van Velzen, on 30-10-2016.
 */
public class Logging {
    private final static String LOG_PREFIX = "Norakomi: ";
    private final static String TAG = Logging.class.getSimpleName();

    private Logging() {
    }

    public static void log(String classTag, String message) {
        Log.v(LOG_PREFIX + classTag, message);
    }

    public static void logError(String classTag, String message) {
        Log.e(LOG_PREFIX + classTag, message);
    }
}
