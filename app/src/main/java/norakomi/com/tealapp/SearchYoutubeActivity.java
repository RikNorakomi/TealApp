package norakomi.com.tealapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.data.model.VideoItem;
import norakomi.com.tealapp.data.service.YoutubeService;

/**
 * Created by Rik van Velzen, on 30-10-2016.
 * <p/>
 * This activity takes care of querying the youtube api with a search keyword
 * More info can be found at:
 * https://developers.google.com/youtube/v3/docs/search
 * https://developers.google.com/youtube/v3/docs/search/list
 */

public class SearchYoutubeActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private Handler handler;
    private ListView videosFound;
    private String searchString;
    private RecyclerView recycler;
    private OverviewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_youtube);
        Logging.log(TAG, "in onCreate()");

        handler = new Handler();
        searchString = "teal swan";

        // setup views
        adapter = new OverviewAdapter();
        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setAdapter(adapter);

        TextView searchQuery = (TextView) findViewById(R.id.activity_search_youtube_search_query);
        searchQuery.setText("Search query: " + searchString);

        searchOnYoutube(searchString);
    }

    private void searchOnYoutube(final String keywords) {
        new Thread() {
            public void run() {

                final YoutubeService youtubeService = new YoutubeService();
                final List<VideoItem> result = youtubeService.search(keywords);
                handler.post(new Runnable() {
                    public void run() {
                        updateVideosFound(result);
                    }
                });
            }
        }.start();
    }

    private void updateVideosFound(List<VideoItem> searchResults) {
        adapter.setContent(searchResults);
    }

}
