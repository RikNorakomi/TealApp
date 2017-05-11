package norakomi.com.tealapp.Utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by Rik van Velzen, Norakomi.com, on 9-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class AndroidVersionSpecific {

    private AndroidVersionSpecific(){}

    public static void removeGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener victim){
        int sdk = Build.VERSION.SDK_INT;
        if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
            removeGlobalLayoutListenerOld(view, victim);
        } else {
            removeGlobalLayoutListenerNew(view, victim);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void removeGlobalLayoutListenerNew(View view, ViewTreeObserver.OnGlobalLayoutListener victim){
        if(view != null && victim != null) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(victim);
        }
    }

    private static void removeGlobalLayoutListenerOld(View view, ViewTreeObserver.OnGlobalLayoutListener victim){
        if(view != null && victim != null) {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(victim);
        }
    }
}
