package norakomi.com.tealapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.Date;
import java.util.List;

import norakomi.com.tealapp.Interfaces.IActionRequestedListener;
import norakomi.com.tealapp.Utils.App;
import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.data.DataManager;
import norakomi.com.tealapp.data.IDataManagerCallback;
import norakomi.com.tealapp.data.model.VideoItem;
import norakomi.com.tealapp.share.ShareVideoTask;

import static norakomi.com.tealapp.Utils.Config.YOUTUBE_SEARCH_STRING;

/**
 * Created by Rik van Velzen, on 30-10-2016.
 * <p/>
 * This activity takes care of querying the youtube api with a search keyword
 * More info can be found at:
 * https://developers.google.com/youtube/v3/docs/search
 * https://developers.google.com/youtube/v3/docs/search/list
 */

public class VideoOverviewActivity extends AppCompatActivity implements IActionRequestedListener {

    private final String TAG = getClass().getSimpleName();

    private RecyclerView recycler;
    private OverviewAdapter adapter;
    private long backButtonClickTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_youtube);


        setupViews();
        getVideos();
    }

    private void setupViews() {
        adapter = new OverviewAdapter(this, false);
        recycler = (RecyclerView) findViewById(R.id.recycler_overview_activity);
        recycler.setAdapter(adapter);

        // remove shadow line from toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
        }
    }

    private void getVideos() {
        DataManager.getInstance().getVideos(YOUTUBE_SEARCH_STRING, new IDataManagerCallback() {
            @Override
            public void onResult(final List<VideoItem> result) {
                adapter.setContent(result);
            }

            @Override
            public void onError(Exception e) {
                // TODO: 8-5-2017 handle error
            }
        });
    }

    @Override
    public void onActionRequested(String videoId, RequestedAction requestedAction) {
        switch (requestedAction) {
            case PLAY_VIDEO:
                Intent intent = new Intent(this, VideoPlayerActivity.class);
                intent.putExtra(VideoPlayerActivity.VIDEO_ID, videoId);
                startActivity(intent);
                break;
            case SHARE:
                VideoItem video = DataManager.getInstance().getVideoFromId(videoId);
                if (video == null) {
                    App.toast("Error: issue sharing video!");
                } else {
                    new ShareVideoTask(video, this);
                }
                break;
            case NOT_INTERESTED:
                break;
            case SAVE_TO_WATCH_LATER:
                break;
            default:
                Logging.logError(TAG, "Unable to handle requested action",
                        new IllegalStateException(requestedAction.name()));
        }
    }

    @Override
    public void onBackPressed() {
        long timestamp = new Date().getTime();

        /* App exit can only be performed by double clickin gon back within specified millisecs. */
        if (timestamp - backButtonClickTime <= 5000) {
            backButtonClickTime = 0;
            super.onBackPressed();
        } else {
            backButtonClickTime = timestamp;
            App.toast("Press again to exit app");
        }
    }
}

