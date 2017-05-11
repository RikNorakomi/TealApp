package norakomi.com.tealapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import norakomi.com.tealapp.Interfaces.IRequestedActionListener;
import norakomi.com.tealapp.Utils.App;
import norakomi.com.tealapp.Utils.AppContext;
import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.data.DataManager;
import norakomi.com.tealapp.data.model.VideoItem;
import norakomi.com.tealapp.share.ShareVideoTask;

import static android.content.ContentValues.TAG;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Rik van Velzen, Norakomi.com, on 10-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class RequestedActionProvider implements IRequestedActionListener {

    private static final RequestedActionProvider mInstance = new RequestedActionProvider();

    public static RequestedActionProvider getInstance() {
        return mInstance;
    }

    private RequestedActionProvider() {
    }

    @Override
    public void onActionRequested(@Nullable String videoId, @NonNull RequestedAction requestedAction) {
        Context context = AppContext.getAppContext();
        switch (requestedAction) {
            case PLAY_VIDEO:
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra(VideoPlayerActivity.VIDEO_ID, videoId);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
            case SHARE:
                VideoItem video = DataManager.getInstance().getVideoFromId(videoId);
                if (video == null) {
                    App.toast("Error: issue sharing video!");
                } else {
                    new ShareVideoTask(video, context);
                }
                break;
            case NOT_INTERESTED:
                break;
            case SAVE_TO_WATCH_LATER:
                break;
            case BOOKMARK:
                DataManager.getInstance().toggleBookmarkedVideo(videoId);
                break;
            default:
                Logging.logError(TAG, "Unable to handle requested action",
                        new IllegalStateException(requestedAction.name()));
        }
    }
}
