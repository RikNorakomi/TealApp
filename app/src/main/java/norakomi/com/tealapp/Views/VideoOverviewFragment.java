package norakomi.com.tealapp.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import norakomi.com.tealapp.Interfaces.IRequestedActionListener;
import norakomi.com.tealapp.OverviewAdapter;
import norakomi.com.tealapp.R;
import norakomi.com.tealapp.Utils.App;
import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.VideoPlayerActivity;
import norakomi.com.tealapp.data.DataManager;
import norakomi.com.tealapp.data.IDataManagerCallback;
import norakomi.com.tealapp.data.model.VideoItem;
import norakomi.com.tealapp.share.ShareVideoTask;

import static android.content.ContentValues.TAG;
import static norakomi.com.tealapp.Utils.Config.YOUTUBE_SEARCH_STRING;

/**
 * Created by Rik van Velzen, Norakomi.com, on 10-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class VideoOverviewFragment extends Fragment implements IRequestedActionListener {

    private OverviewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_video_overview, container, false);

        setupViews(rootView);
        getVideos();

        return rootView;
    }

    private void setupViews(ViewGroup rootView) {
        adapter = new OverviewAdapter(this);
        RecyclerView recycler = (RecyclerView) rootView.findViewById(R.id.recycler_video_overview);
        recycler.setAdapter(adapter);
    }

    private void getVideos() {
        DataManager.getInstance().getVideos(YOUTUBE_SEARCH_STRING, new IDataManagerCallback() {
            @Override
            public void onResult(final List<VideoItem> result) {
                adapter.setRecyclerVideos(result);
            }

            @Override
            public void onError(Exception e) {
                // TODO: 8-5-2017 handle error
            }
        });
    }

    @Override
    public void onActionRequested(@Nullable String videoId, @NonNull RequestedAction requestedAction) {
        Context context = getContext();
        switch (requestedAction) {
            case PLAY_VIDEO:
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra(VideoPlayerActivity.VIDEO_ID, videoId);
                startActivity(intent);
                break;
            case SHARE:
                VideoItem video = DataManager.getInstance().getVideoFromId(videoId);
                if (video == null) {
                    App.toast("Error: issue sharing video!");
                } else {
                    new ShareVideoTask(video, context);
                }
                break;
            case NOT_INTERESTED:
                break;
            case SAVE_TO_WATCH_LATER:
                break;
            case BOOKMARK:
                DataManager.getInstance().toggleBookmarkedVideo(videoId);
                break;
            default:
                Logging.logError(TAG, "Unable to handle requested action",
                        new IllegalStateException(requestedAction.name()));
        }
    }
}
