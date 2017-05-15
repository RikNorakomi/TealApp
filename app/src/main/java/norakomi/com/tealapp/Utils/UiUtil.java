package norakomi.com.tealapp.Utils;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by Rik van Velzen, Norakomi.com, on 12-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class UiUtil {

    private UiUtil() {}

    /**
     * https://developer.android.com/training/snackbar/action.html
     * add action f.e.: mySnackbar.setAction(R.string.undo_string, new MyUndoListener());
     */
    public static void showSnackbarLong(FragmentActivity activity, String s) {
        View view = activity.findViewById(android.R.id.content); // gets rootview
        Snackbar.make(view, s, Snackbar.LENGTH_LONG).show();
    }

    public static void showSnackbarLong(Activity activity, String s) {
        View view = activity.findViewById(android.R.id.content); // gets rootview
        Snackbar.make(view, s, Snackbar.LENGTH_LONG).show();
    }
}
