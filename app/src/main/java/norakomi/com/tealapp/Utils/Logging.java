package norakomi.com.tealapp.Utils;

import android.support.annotation.NonNull;
import android.util.Log;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by Rik van Velzen, on 30-10-2016.
 */
public class Logging {
    private final static String LOG_PREFIX = "Norakomi: ";

    private static String logString = "";

    private static BehaviorSubject<String> logStringSubject = BehaviorSubject.create();
    private static boolean enableLogObservable = true;

    private Logging() {
    }

    public static void log(
            @NonNull String classTag,
            @NonNull String message) {
        logStringSubject.onNext(classTag + message);
        Log.v(LOG_PREFIX + classTag, message);
    }

    public static void logError(
            @NonNull String classTag,
            @NonNull String message) {
        logStringSubject.onNext(classTag + message);
        Log.e(LOG_PREFIX + classTag, message);
    }

    public static void logError(
            @NonNull String classTag,
            @NonNull String message,
            @NonNull Exception e) {
        String exceptionMessage = " exceptionToString = " + e.toString();
        logStringSubject.onNext(classTag + message + exceptionMessage);
        Log.e(LOG_PREFIX + classTag, message + exceptionMessage);
    }

    public static void log(String classTag, String message, boolean b) {
        Log.v(LOG_PREFIX + classTag, message);
    }

    public static Observable<String> getLogOutputRx() {
        return logStringSubject;
    }


}
