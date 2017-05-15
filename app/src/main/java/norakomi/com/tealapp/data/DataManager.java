package norakomi.com.tealapp.data;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;
import norakomi.com.tealapp.Utils.App;
import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.data.model.BookmarkedVideoId;
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

    private List<VideoItem> readFromRealm(Object o) {
        return mRealmController.getVideosFromRealm();
    }

    // FIXME: 15-5-2017
    private Object writeToRealm(List<VideoItem> videoItems) {
        return mRealmController.cacheVideos(videoItems);
    }

    private ArrayList<VideoItem> getVideosFromCache() {
        return cachedResults;
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

    /**
     * This method returns a new collection of video items depending on requested set of string Id
     *
     * @param videoIds
     * @return Collection of VideoItems
     */
    public List<VideoItem> getVideosFromIdSet(Set<String> videoIds) {
        List<VideoItem> videoItems = new ArrayList<>();
        for (VideoItem video : cachedResults) {
            if (videoIds.contains(video.getId())) {
                videoItems.add(video);
            }
        }

        Logging.log(TAG, "in getVideosFromIdSet. Return size = " + videoItems.size());

        return videoItems;
    }

    public void toggleBookmarkedVideo(String videoId) {
        boolean bookmarkedVideoPresentInRealm = false;

        RealmController realm = RealmController.getInstance();

        RealmResults<BookmarkedVideoId> bookmarkedVideos = realm.getBookmarkedVideoIDs();
        Logging.log(TAG, "in toggleBookmarkedVideo for videoId: " + videoId);

        // check if video
        for (BookmarkedVideoId bookmarkedVideoId : bookmarkedVideos) {
            if (bookmarkedVideoId.getVideoId().equals(videoId)) {
                Logging.log(TAG, "Found video bookmarked");
                bookmarkedVideoPresentInRealm = true;
                break;
            }
        }
        if (bookmarkedVideoPresentInRealm) {
            realm.removeBookmarkedVideo(videoId);
        } else {
            realm.addBookmarkedVideo(videoId);
        }
    }

    public boolean isVideoBookmarked(@NonNull String videoId) {
        RealmResults<BookmarkedVideoId> bookmarkedVideos = RealmController.getInstance().getBookmarkedVideoIDs();
        Logging.log(TAG, "in isVideoBookmarked for videoId: " + videoId);
        for (BookmarkedVideoId bookmarkedVideoId : bookmarkedVideos) {
            if (bookmarkedVideoId.getVideoId().equals(videoId)) {
                Logging.log(TAG, "Found video to be bookmarked");
                return true;
            }
        }

        return false;
    }

    /**
     * Creates a VideoItem Collection for Bookmarked Videos
     *
     * @return
     */
    public List<VideoItem> getBookmarkedVideos() {
        RealmResults<BookmarkedVideoId> bookmarkedVideoIds = RealmController.getInstance().getBookmarkedVideoIDs();

        List<VideoItem> cachedVideos = getVideosFromCache();
        List<VideoItem> bookmarkedVideos = new ArrayList<>();
        Logging.log(TAG, "in getBookmarkedVideos:");

        // FIXME: 11-5-2017 this is sub optimal for performance
        for (VideoItem video : cachedVideos) {
            if (isVideoBookmarked(video, bookmarkedVideoIds)) {
                bookmarkedVideos.add(video);
            }
        }

        Logging.log(TAG, "returning bookmarked videos with size: " + bookmarkedVideoIds.size());

        return bookmarkedVideos;
    }

    private boolean isVideoBookmarked(VideoItem video, RealmResults<BookmarkedVideoId> bookmarkedVideoIds) {
        for (BookmarkedVideoId bookmarkedVideoId : bookmarkedVideoIds) {
            if (bookmarkedVideoId.getVideoId().equals(video.getId())) {
                return true;
            }
        }

        return false;
    }

    public boolean isVideoThumbedUp(String videoId) {
        return false;
    }

    public boolean isVideoThumbedDown(String videoId) {
        return false;
    }

    public void toggleThumbDownVideo(String videoId) {

    }

    public void toggleThumbUpVideo(String videoId) {

    }
}
