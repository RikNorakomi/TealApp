package norakomi.com.tealapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import norakomi.com.tealapp.data.DataManager;
import norakomi.com.tealapp.data.IDataManagerCallback;
import norakomi.com.tealapp.data.model.VideoItem;

import static norakomi.com.tealapp.Utils.Config.YOUTUBE_SEARCH_STRING;

/**
 * Created by Rik van Velzen, on 30-10-2016.
 * <p/>
 * This activity takes care of querying the youtube api with a search keyword
 * More info can be found at:
 * https://developers.google.com/youtube/v3/docs/search
 * https://developers.google.com/youtube/v3/docs/search/list
 */

public class SearchYoutubeActivity extends AppCompatActivity implements IVideoClickedCallback {

    private final String TAG = getClass().getSimpleName();

    private RecyclerView recycler;
    private OverviewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_youtube);

        setupViews();
        getVideos();
    }

    private void setupViews() {
        adapter = new OverviewAdapter(this);
        recycler = (RecyclerView) findViewById(R.id.recycler_overview_activity);
        recycler.setAdapter(adapter);
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
    public void videoClickedWithId(String videoId) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(PlayerActivity.VIDEO_ID, videoId);
        startActivity(intent);
    }
}

