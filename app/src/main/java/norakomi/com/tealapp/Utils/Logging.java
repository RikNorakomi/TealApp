package norakomi.com.tealapp.Utils;

import android.support.annotation.NonNull;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

import static norakomi.com.tealapp.Utils.Config.LOG_PREFIX;

/**
 * Created by Rik van Velzen, on 30-10-2016.
 */
public class Logging {

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
            @NonNull Throwable t) {
        String exceptionMessage = " exceptionToString = " + t.toString();
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
