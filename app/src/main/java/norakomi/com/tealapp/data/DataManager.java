package norakomi.com.tealapp.data;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import norakomi.com.tealapp.Utils.App;
import norakomi.com.tealapp.data.model.VideoItem;
import norakomi.com.tealapp.data.service.YoutubeService;

/**
 * Created by Rik van Velzen, Norakomi.com, on 7-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class DataManager {

    private final String TAG = getClass().getSimpleName();

    private static final DataManager ourInstance = new DataManager();
    private final HashMap<String, List<VideoItem>> cachedQueries = new HashMap();
    private ArrayList<VideoItem> cachedResults = new ArrayList<>();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }

    /**
     * This method creates a background thread to get the result from a youtube query
     * The result is posted back on the UI-thread (getMainLooper...) via the callback interface
     *
     * @param searchQuery          search query that needs to be performed. By convention
     *                             {@link norakomi.com.tealapp.Utils.Config} holds Constant for this string
     * @param iDataManagerCallback callback interface
     */
    public void getVideos(String searchQuery, IDataManagerCallback iDataManagerCallback) {
        if (cachedQueries.containsKey(searchQuery)) {
            App.log(TAG, "getting video results from cache");
            iDataManagerCallback.onResult(cachedQueries.get(searchQuery));
        } else {
            try {
                new Thread() {
                    public void run() {
                        App.log(TAG, "getting video results from youtube");
                        // create youtube service and perform query
                        final YoutubeService youtubeService = new YoutubeService();
                        final List<VideoItem> result = youtubeService.search(searchQuery);

                        // post results back on ui thread
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> iDataManagerCallback.onResult(result));

                        // cache result & search query
                        cachedQueries.put(searchQuery, result);
                        cachedResults = new ArrayList<>(result);
                    }
                }.start();
            } catch (Exception e) {
                iDataManagerCallback.onError(e);
            }
        }
    }

    /**
     * This method queries the cached search query results for requested id
     *
     * @param videoId
     * @return video item from cache or null if video id not found
     */
    @Nullable
    public VideoItem getVideoFromId(String videoId) {
        for (VideoItem video : cachedResults) {
            if (video.getId().equals(videoId)) {
                return video;
            }
        }

        return null;
    }
}
