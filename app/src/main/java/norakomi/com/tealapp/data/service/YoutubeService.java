package norakomi.com.tealapp.data.service;

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

import norakomi.com.tealapp.Utils.Config;
import norakomi.com.tealapp.R;
import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.Utils.AppContext;
import norakomi.com.tealapp.data.model.VideoItem;

/**
 * Created by Rik van Velzen, on 30-10-2016.
 */
public class YoutubeService {

    /**
     * Base url for youtube queries. Some common query params:
     * <p>
     * q:           The q parameter specifies the query term to search for. Your request can also
     * use the Boolean NOT (-) and OR (|) operators to exclude videos or to find videos
     * that are associated with one of several search terms.
     * maxResults:  The maxResults parameter specifies the maximum number of items that should be returned
     * in the result set. Acceptable values are 0 to 50, inclusive. The default value is 5.
     * order:       date, rating, relevance, title, videoCount, viewCount
     * pageToken:   The pageToken parameter identifies a specific page in the result set that should be
     * returned. In an API response, the nextPageToken and prevPageToken properties identify
     * other pages that could be retrieved.
     */

    private final String TAG = getClass().getSimpleName();

    private YouTube.Search.List query;

    // Your developer key goes here
    public static final String YOUTUBE_API_KEY = Config.TEAL_APP_YOUTUBE_API_KEY;
    private static final long maxResults = 50;

    private static YoutubeService instance;

    public static YoutubeService getInstance() {
        if (instance == null) {
            instance = new YoutubeService();
        }
        return instance;
    }

    public YoutubeService() {
        YouTube youtube = new YouTube.Builder(
                new NetHttpTransport(),
                new JacksonFactory(),
                new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest hr) throws IOException {
                        Logging.logError(TAG, "YoutubeService init: HttpRequest = " + hr.toString());
                    }
                })
                .setApplicationName(AppContext.getAppContext().getString(R.string.app_name)).build();

        try {
            query = youtube.search().list("id,snippet");
            query.setKey(YOUTUBE_API_KEY);
            query.setType("video");
            query.setMaxResults(maxResults);

            // For available fields check json at: https://developers.google.com/youtube/v3/docs/search
            // to get all available fields just don't set fields
            query.setFields("items(" +
                    "id/videoId," +
                    "snippet/description," +
                    "snippet/publishedAt," +
                    "snippet/title," +
//                    "snippet/categoryid," +
                    "snippet/thumbnails/high/url)");
        } catch (IOException e) {
            Log.d("YC", "Could not initialize: " + e);
        }
    }

    public List<VideoItem> search(String keywords) {
        query.setQ(keywords);
        try {
            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();

            List<VideoItem> items = new ArrayList<>();
            int e = 1;
            for (SearchResult result : results) {
                VideoItem item = new VideoItem();
                item.setTitle(result.getSnippet().getTitle());
                item.setDescription(result.getSnippet().getDescription());
                item.setThumbnailURL(result.getSnippet().getThumbnails().getHigh().getUrl());
                item.setId(result.getId().getVideoId());

                items.add(item);
                if (e == 1) {
                    Logging.logError(TAG, "results =  " + result.toString());
                    e++;
                }
            }
            return items;
        } catch (IOException e) {
            Logging.log(TAG, "Could not search: " + e);
            return null;

        }
    }
}

