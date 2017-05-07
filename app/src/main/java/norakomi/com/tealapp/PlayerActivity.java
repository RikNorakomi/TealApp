package norakomi.com.tealapp;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import norakomi.com.tealapp.Utils.Config;
import norakomi.com.tealapp.Utils.SharedPrefs;

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayerView playerView;
    int rotationAngle = 0;
    boolean descriptionCollapsed = true;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_player);

        initializeYoutubePlayerView();
        setupViews();
    }

    private void initializeYoutubePlayerView() {
        playerView = (YouTubePlayerView) findViewById(R.id.player_view);
        playerView.initialize(Config.TEAL_APP_YOUTUBE_API_KEY, this);
    }

    private void setupViews() {
        // setup whether autoplay is enabled
        final SwitchCompat switchCompat = (SwitchCompat) findViewById(R.id.autoplay_switch);
        switchCompat.setChecked(SharedPrefs.getInstance().isAutoplayEnabled());
        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefs.getInstance().setAutoplayEnabled(switchCompat.isChecked());
            }
        });

        // setup clickListener on Title box to animate expand/collapse icon and description
        View titleContainer = findViewById(R.id.title_container);
        final View v = findViewById(R.id.expand_collapse_icon);
        titleContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int targetRotationAngle = descriptionCollapsed ? rotationAngle + 180 : rotationAngle - 180;
                ObjectAnimator anim = ObjectAnimator.ofFloat(v, "rotation", rotationAngle, targetRotationAngle);
                anim.setDuration(500);
                anim.start();
                rotationAngle += 180;
                descriptionCollapsed = !descriptionCollapsed;
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
}