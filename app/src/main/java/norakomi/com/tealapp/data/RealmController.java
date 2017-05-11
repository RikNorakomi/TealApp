package norakomi.com.tealapp.data;

import android.support.annotation.NonNull;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.data.model.BookmarkedVideoId;

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

    public static RealmController getInstance() {
        return ourInstance;
    }

    private RealmController() {
        Logging.log(TAG, "in constructor. Getting Realm default instance");
        realm = Realm.getDefaultInstance();
    }


    /**
     * @return a list of {@link BookmarkedVideoId} which hold id's for bookmarked videos
     */
    public RealmResults<BookmarkedVideoId> getBookmarkedVideoIDs() {
        RealmQuery<BookmarkedVideoId> query = realm.where(BookmarkedVideoId.class);
        RealmResults<BookmarkedVideoId> results = query.findAll();

        Logging.log(TAG, "in getBookmarkedVideo. ResultSize = " + results.size());

        return results;
    }

    /**
     * Removes id from bookmarked video id's
     *
     * @param videoId
     */
    public void removeBookmarkedVideo(String videoId) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<BookmarkedVideoId> result = realm.
                        where(BookmarkedVideoId.class).
                        equalTo("videoId", videoId).
                        findAll();
                Logging.log(TAG, "Deleting videos with id: " + videoId + ", amountOfDeleted: " + result.size());
                result.deleteAllFromRealm();
            }
        });
    }

    public void addBookmarkedVideo(@NonNull String videoId) {
        // check if videoId has already been stored
        RealmResults<BookmarkedVideoId> bookmarkedVideos = getBookmarkedVideoIDs();
        for (BookmarkedVideoId video : bookmarkedVideos) {
            if (video.getVideoId().equals(videoId)) {
                Logging.log(TAG, "already bookmarked video with id: " + videoId);
                // video already bookmarked: do nothing
                return;
            }
        }

        Logging.log(TAG, "bookmarkedVideoSize before adding: " + bookmarkedVideos.size());

        // write object to Realm
        realm.beginTransaction();
        realm.createObject(BookmarkedVideoId.class, videoId);
        realm.commitTransaction();
        Logging.log(TAG, "storing bookmarked video with id: " + videoId);

        Logging.log(TAG, "bookmarkedVideoSize before adding: " + getBookmarkedVideoIDs().size());


    }
}
