package norakomi.com.tealapp.data.service;

import android.content.Context;
import android.util.Log;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import config.Config;
import norakomi.com.tealapp.R;
import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.data.model.VideoItem;

/**
 * Created by Rik van Velzen, on 30-10-2016.
 */
public class YoutubeService {

    private final String TAG = getClass().getSimpleName();

    private YouTube youtube;
    private YouTube.Search.List query;

    // Your developer key goes here
    public static final String YOUTUBE_API_KEY = Config.TEAL_APP_YOUTUBE_API_KEY;
    private final long maxResults = 50;

    public YoutubeService(Context context) {
        youtube = new YouTube.Builder(
                new NetHttpTransport(),
                new JacksonFactory(),
                new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest hr) throws IOException {
                        Logging.log(TAG, "YoutubeService init: HttpRequest = " +hr.toString());
                    }
                })
                .setApplicationName(context.getString(R.string.app_name)).build();

        try {
            query = youtube.search().list("id,snippet");
            query.setKey(YOUTUBE_API_KEY);
            query.setType("video");
            query.setMaxResults(maxResults);
            query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
        } catch (IOException e) {
            Log.d("YC", "Could not initialize: " + e);
        }
    }

    public List<VideoItem> search(String keywords) {
        query.setQ(keywords);
        try {
            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();

            List<VideoItem> items = new ArrayList<VideoItem>();
            for (SearchResult result : results) {
                VideoItem item = new VideoItem();
                item.setTitle(result.getSnippet().getTitle());
                item.setDescription(result.getSnippet().getDescription());
                item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                item.setId(result.getId().getVideoId());
                items.add(item);
            }
            return items;
        } catch (IOException e) {
            Logging.log(TAG, "Could not search: " + e);
            return null;

        }
    }
}

