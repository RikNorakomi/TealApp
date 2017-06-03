package norakomi.com.tealapp.data;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.realm.Realm;
import io.realm.RealmResults;
import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.data.model.VideoItem;

/**
 * Created by Rik van Velzen, Norakomi.com, on 11-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

class RealmController {

    private final String TAG = getClass().getSimpleName();
    private static final RealmController ourInstance = new RealmController();
    private final Realm realm;
    private RealmResults<VideoItem> bookmarkedVideos;

    public static RealmController getInstance() {
        return ourInstance;
    }

    private RealmController() {
        Logging.log(TAG, "in constructor. Getting Realm default instance");
        realm = Realm.getDefaultInstance();
    }

    public List<VideoItem> getVideosFromRealm() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(VideoItem.class).findAll();
    }

    public VideoItem getVideosFromId(String videoId) {
        return realm.where(VideoItem.class).equalTo("id", videoId).findFirst();
    }


    public Observable<List<VideoItem>> getBookmarkedVideosFromRealmRx() {
        PublishSubject<List<VideoItem>> subject = PublishSubject.create();

        bookmarkedVideos = realm.where(VideoItem.class).equalTo("isBookmarked", true).findAll();
        Logging.log(TAG, "in getBookmarkedVideosFromRealmRx.size = " + bookmarkedVideos.size());
        subject.onNext(bookmarkedVideos);
        bookmarkedVideos.addChangeListener(element -> {
            Logging.log(TAG, "in getBookmarkedVideosFromRealmRx.onChange. size new results = " + element.size());
            subject.onNext(element);
        });

        return subject;
    }

    // FIXME: 15-5-2017 return type
    public Object cacheVideos(List<VideoItem> videoItems) {
        // SAY YES TO THIS
//        Realm realm = null;
//        try { // I could use try-with-resources here
//            realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.insertOrUpdate(videoItems));
//        } finally {
//            if (realm != null) {
//                realm.close();
//            }
//        }


        return new Object();
    }

    public void setBookmarkVideo(String videoId, boolean bookmark) {
        Logging.log(TAG, "in addBookmarkedVideo for videoID: " + videoId);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<VideoItem> result = realm.
                        where(VideoItem.class).
                        equalTo("id", videoId).
                        findAll();


                for (int i = 0; i < result.size(); i++) {
                    result.get(i).setBookmarked(bookmark);
                    Logging.log(TAG, "Setting video with id:" + videoId + " to bookmarked = " + bookmark);
                }
            }
        });
    }

    public void setThumbedDown(String videoId, boolean thumbedDown) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<VideoItem> result = realm.
                        where(VideoItem.class).
                        equalTo("id", videoId).
                        findAll();


                // Setting thumbed down value on video with id
                // If thumbed down is set to true, we have to check if thumbed up
                // current value is true as well and if so change to false
                for (int i = 0; i < result.size(); i++) {
                    result.get(i).setThumbedDown(thumbedDown);
                    Logging.log(TAG, "Setting video with id:" + videoId + " to thumbedDown = " + thumbedDown);
                }
            }
        });
    }

    public void setThumbedUp(String videoId, boolean thumbedUp) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<VideoItem> result = realm.
                        where(VideoItem.class).
                        equalTo("id", videoId).
                        findAll();


                // Setting thumbed down value on video with id
                // If thumbed down is set to true, we have to check if thumbed up
                // current value is true as well and if so change to false
                for (int i = 0; i < result.size(); i++) {
                    result.get(i).setThumbedUp(thumbedUp);
                    Logging.log(TAG, "Setting video with id:" + videoId + " to thumbedUp = " + thumbedUp);
                }
            }
        });
    }

    public boolean isVideoBookmarked(String videoId) {
        VideoItem video = realm.where(VideoItem.class).equalTo("id", videoId).findFirst();
        return video.isBookmarked();
    }

    public boolean isThumbedDown(String videoId) {
        VideoItem video = realm.where(VideoItem.class).equalTo("id", videoId).findFirst();
        return video.isThumbedDown();
    }

    public boolean isThumbedUp(String videoId) {
        VideoItem video = realm.where(VideoItem.class).equalTo("id", videoId).findFirst();
        return video.isThumbedUp();
    }


}
