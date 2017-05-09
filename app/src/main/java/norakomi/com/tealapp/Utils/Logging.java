package norakomi.com.tealapp.Utils;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by Rik van Velzen, on 30-10-2016.
 */
public class Logging {
    private final static String LOG_PREFIX = "Norakomi: ";

    private Logging() {
    }

    public static void log(
            @NonNull String classTag,
            @NonNull String message) {
        Log.v(LOG_PREFIX + classTag, message);
    }

    public static void logError(
            @NonNull String classTag,
            @NonNull String message) {
        Log.e(LOG_PREFIX + classTag, message);
    }

    public static void logError(
            @NonNull String classTag,
            @NonNull String message,
            @NonNull Exception e) {
        String exceptionMessage = " exceptionToString = " + e.toString();
        Log.e(LOG_PREFIX + classTag, message + exceptionMessage);
    }
}
