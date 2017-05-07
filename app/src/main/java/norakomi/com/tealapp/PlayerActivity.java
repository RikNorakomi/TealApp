package norakomi.com.tealapp;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.List;

import norakomi.com.tealapp.Utils.App;
import norakomi.com.tealapp.Utils.Config;
import norakomi.com.tealapp.Utils.SharedPrefs;
import norakomi.com.tealapp.data.DataManager;
import norakomi.com.tealapp.data.IDataManagerCallback;
import norakomi.com.tealapp.data.model.VideoItem;

import static norakomi.com.tealapp.Utils.Config.YOUTUBE_SEARCH_STRING;

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, IVideoClickedCallback {

    public static final String VIDEO_ID = "VIDEO_ID";
    int rotationAngle = 0;
    boolean descriptionCollapsed = true;
    private OverviewAdapter adapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_player);

        initializeYoutubePlayerView();
        setupViews();
        getVideos();
    }

    private void initializeYoutubePlayerView() {
        YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.player_view);
        playerView.initialize(Config.TEAL_APP_YOUTUBE_API_KEY, this);
    }

    private void setupViews() {
        // setup whether autoplay is enabled
        final SwitchCompat switchCompat = (SwitchCompat) findViewById(R.id.autoplay_switch);
        switchCompat.setChecked(SharedPrefs.getInstance().isAutoplayEnabled());
        switchCompat.setOnClickListener(view -> SharedPrefs.getInstance().setAutoplayEnabled(switchCompat.isChecked()));

        // setup clickListener on Title box to animate expand/collapse icon and description
        View titleContainer = findViewById(R.id.title_container);
        final View v = findViewById(R.id.expand_collapse_icon);
        titleContainer.setOnClickListener(view -> {
            int targetRotationAngle = descriptionCollapsed ? rotationAngle + 180 : rotationAngle - 180;
            ObjectAnimator anim = ObjectAnimator.ofFloat(v, "rotation", rotationAngle, targetRotationAngle);
            anim.setDuration(500);
            anim.start();
            rotationAngle += 180;
            descriptionCollapsed = !descriptionCollapsed;
        });

        adapter = new OverviewAdapter(this);
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_player_activity);
        recycler.setAdapter(adapter);
    }

    public void getVideos() {
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
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult result) {
        Toast.makeText(this, getString(R.string.player_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean restored) {
        if (!restored) {
            player.cueVideo(getIntent().getStringExtra("VIDEO_ID"));
        }
    }


    @Override
    public void videoClickedWithId(String videoId) {
        // TODO: 8-5-2017
        App.toast("video clicked with id: " + videoId);
    }
}