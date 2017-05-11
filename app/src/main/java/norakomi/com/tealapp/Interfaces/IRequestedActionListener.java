package norakomi.com.tealapp.Interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Rik van Velzen, Norakomi.com, on 8-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public interface IRequestedActionListener {

    enum RequestedAction {
        // Actions for VideoOverviewActivity
        PLAY_VIDEO,
        NOT_INTERESTED,
        SAVE_TO_WATCH_LATER,
        SHARE,
        BOOKMARK,

        // Actions for VideoPlayerActivity
        TOGGLE_SHOW_DESCRIPTION,
        SHOW_COMMENTS,
        THUMB_DOWN,
        THUMB_UP,
        TOGGLE_AUTOPLAY
    }

    void onActionRequested(@Nullable String videoId, @NonNull RequestedAction requestedAction);
}
