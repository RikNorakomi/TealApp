package norakomi.com.tealapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import norakomi.com.tealapp.Interfaces.IRequestedActionListener;
import norakomi.com.tealapp.Utils.App;
import norakomi.com.tealapp.Utils.Config;
import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.Utils.SharedPrefs;
import norakomi.com.tealapp.Utils.UiNotification;
import norakomi.com.tealapp.data.DataManager;
import norakomi.com.tealapp.data.model.VideoItem;
import norakomi.com.tealapp.share.ShareVideoTask;

public class VideoPlayerActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener,
        IRequestedActionListener {

    private final String TAG = getClass().getSimpleName();

    public static final String VIDEO_ID = "VIDEO_ID";
    private OverviewAdapter adapter;
    private Disposable disposable;
    private String mVideoId;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_player);

        mVideoId = getIntent().getStringExtra(VIDEO_ID);
        Logging.log(TAG, "videoID to play: " + mVideoId);

        setupViews();
    }

    private void setupViews() {
        YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.player_view);
        playerView.initialize(Config.TEAL_APP_YOUTUBE_API_KEY, this);

        adapter = new OverviewAdapter(this, DataManager.getInstance().getVideoFromId(mVideoId));
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_player_activity);
        recycler.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Using Rx here because we could have one result for cached items and one result for api call
        Observable<List<VideoItem>> observable =
                DataManager.getInstance().getVideosRx()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());

        disposable = observable.subscribe(videoItems -> adapter.setRecyclerVideos(videoItems), this::onErrorGetVideos);
    }

    private void onErrorGetVideos(Throwable throwable) {
        String message = "Error getting videos";
        Logging.logError(TAG, message, throwable);
        UiNotification.showSnackbarLong(this, getString(R.string.overview_error_loading_videos));
    }

    @Override
    protected void onPause() {
        super.onPause();
        disposable.dispose();
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult result) {
        App.toastLong(getString(R.string.player_error));
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean restored) {
        if (!restored) {
            player.cueVideo(mVideoId);
        }
    }

    @Override
    public void onActionRequested(@Nullable String videoId, @NonNull RequestedAction requestedAction) {
        Logging.log(TAG, "requestedAction = " + requestedAction.name());

        switch (requestedAction) {
            case TOGGLE_SHOW_DESCRIPTION:
                Logging.log(TAG, "show description clicked");
                break;
            case SHOW_COMMENTS:
                Logging.log(TAG, "show comments clicked");
                break;
            case THUMB_DOWN:
                Logging.log(TAG, "thumbs down clicked");
                break;
            case THUMB_UP:
                Logging.log(TAG, "thumbs up clicked");
                break;
            case SHARE:
                Logging.log(TAG, "share clicked");
                VideoItem video = DataManager.getInstance().getVideoFromId(mVideoId);
                if (video == null) {
                    App.toast("Error: issue sharing video!");
                } else {
                    new ShareVideoTask(video, this);
                }
                break;
            case PLAY_VIDEO:
                App.toast("video clicked with id: " + videoId);
                break;
            case NOT_INTERESTED:
                Logging.log(TAG, "NOT_INTERESTED clicked");
                break;
            case SAVE_TO_WATCH_LATER:
                Logging.log(TAG, "SAVE_TO_WATCH_LATER clicked");
                break;
            case BOOKMARK:
                DataManager.getInstance().toggleBookmarkedVideo(mVideoId);
                break;
            case TOGGLE_AUTOPLAY:
                Logging.log(TAG, "toggle autoplay clicked");
                SharedPrefs.getInstance().toggleAutoplay();
                break;
            default:


        }
    }
}