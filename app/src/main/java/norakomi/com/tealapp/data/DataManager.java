package norakomi.com.tealapp.data;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import norakomi.com.tealapp.Utils.App;
import norakomi.com.tealapp.Utils.Logging;
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
    private final RealmController mRealmController;
    private ArrayList<VideoItem> cachedResults = new ArrayList<>();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
        mRealmController = RealmController.getInstance();
    }

    /**
     * This method creates a background thread to get the result from a youtube query
     * The result is posted back on the UI-thread (getMainLooper...) via the callback interface
     *
     * @param searchQuery          search query that needs to be performed. By convention
     *                             {@link norakomi.com.tealapp.Utils.Config} holds Constant for this string
     * @param iDataManagerCallback callback interface
     */
    @Deprecated
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
     * Returns an observable of List<VideoItem> for a youtube video query
     * Also check to see if realm has any videos cached and if so merge the result with the observable
     */
    public Observable<List<VideoItem>> getVideosRx(String searchQuery) {
        Observable<List<VideoItem>> observable =
                Observable.fromCallable(() -> {
                    // create youtube service and perform query
                    return new YoutubeService().search(searchQuery);
                })
                        .subscribeOn(Schedulers.io())
                        // Write result to Realm on Computation scheduler to cache data
                        .observeOn(Schedulers.computation())
                        .map(this::writeToRealm) // FIXME: 15-5-2017
                        // Read results in Android Main Thread (UI)
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(this::readFromRealm); // FIXME: 15-5-2017

        List<VideoItem> cachedVideoItems = mRealmController.getVideosFromRealm();
        if (cachedVideoItems != null) {
            Logging.log(TAG, "Found cached videoItems in realm. Merging observable");
            // Merge with the observable from youtube api call
            observable = observable.mergeWith(Observable.just(cachedVideoItems));
        } else {
            Logging.log(TAG, "No cached videoItems found in realm. Not Merging observable");
        }

        return observable;
    }

    public Observable<List<VideoItem>> getBookmarkedVideosRx() {
        return mRealmController.getBookmarkedVideosFromRealmRx()
                .subscribeOn(Schedulers.io())
                // Read results in Android Main Thread (UI)
                .observeOn(AndroidSchedulers.mainThread());
    }


    private List<VideoItem> readFromRealm(Object o) {
        return mRealmController.getVideosFromRealm();
    }

    // FIXME: 15-5-2017
    private Object writeToRealm(List<VideoItem> videoItems) {
        return mRealmController.cacheVideos(videoItems);
    }

    /**
     * @param videoId
     * @return video item from realm
     */
    @Nullable
    public VideoItem getVideoFromId(String videoId) {
        return mRealmController.getVideosFromId(videoId);
    }

    public boolean isVideoBookmarked(@NonNull String videoId) {
        VideoItem videoItem = RealmController.getInstance().getVideosFromId(videoId);
        return videoItem.isBookmarked();
    }

    public boolean isVideoThumbedUp(@NonNull String videoId) {
        VideoItem videoItem = RealmController.getInstance().getVideosFromId(videoId);
        return videoItem.isThumbedUp();
    }

    public boolean isVideoThumbedDown(@NonNull String videoId) {
        VideoItem videoItem = RealmController.getInstance().getVideosFromId(videoId);
        return videoItem.isThumbedDown();
    }

    public void toggleBookmarkedVideo(String videoId) {
        boolean isBookmarked = mRealmController.isVideoBookmarked(videoId);
        Logging.log(TAG, "in toggleBookmarkedVideo for id:" + videoId + " ,bookmarked=" + isBookmarked);
        mRealmController.setBookmarkVideo(videoId, !isBookmarked);
    }

    public void toggleThumbDownVideo(@NonNull String videoId) {
        boolean isThumbedDown = mRealmController.isThumbedDown(videoId);
        mRealmController.setThumbedDown(videoId, !isThumbedDown);
    }

    public void toggleThumbUpVideo(@NonNull String videoId) {
        boolean isThumbedDown = mRealmController.isThumbedUp(videoId);
        mRealmController.setThumbedUp(videoId, !isThumbedDown);

    }
}
