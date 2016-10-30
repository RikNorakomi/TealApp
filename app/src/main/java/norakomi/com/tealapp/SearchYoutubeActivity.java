package norakomi.com.tealapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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

    private List<VideoItem> searchResults;
    private Handler handler;
    private ListView videosFound;
    private String searchString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_youtube);
        Logging.log(TAG, "in onCreate()");

        videosFound = (ListView) findViewById(R.id.videos_found);
        handler = new Handler();
        searchString = "teal swan";

        TextView searchQuery = (TextView) findViewById(R.id.activity_search_youtube_search_query);
        searchQuery.setText("Search query: " + searchString);
        searchOnYoutube(searchString);
    }

    private void searchOnYoutube(final String keywords) {
        new Thread() {
            public void run() {

                YoutubeService youtubeService = new YoutubeService();
                searchResults = youtubeService.search(keywords);
                handler.post(new Runnable() {
                    public void run() {
                        updateVideosFound();
                    }
                });
            }
        }.start();
    }

    private void updateVideosFound() {
        ArrayAdapter<VideoItem> adapter = new ArrayAdapter<VideoItem>(
                getApplicationContext(),
                R.layout.video_item,
                searchResults) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.video_item, parent, false);
                }
                ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView) convertView.findViewById(R.id.video_title);
                TextView description = (TextView) convertView.findViewById(R.id.video_description);

                VideoItem searchResult = searchResults.get(position);

                Glide.with(getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                title.setText(searchResult.getTitle());
                description.setText(searchResult.getDescription());
                return convertView;
            }
        };

        videosFound.setAdapter(adapter);
        addClickListener();
    }

    private void addClickListener() {
        videosFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                Logging.log(TAG, searchResults.get(pos).toString());
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("VIDEO_ID", searchResults.get(pos).getId());
                startActivity(intent);
            }


        });
    }
}
