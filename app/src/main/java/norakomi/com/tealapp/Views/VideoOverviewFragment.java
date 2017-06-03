package norakomi.com.tealapp.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import norakomi.com.tealapp.Interfaces.IRequestedActionListener;
import norakomi.com.tealapp.OverviewAdapter;
import norakomi.com.tealapp.R;
import norakomi.com.tealapp.Utils.App;
import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.Utils.UiNotification;
import norakomi.com.tealapp.VideoPlayerActivity;
import norakomi.com.tealapp.data.DataManager;
import norakomi.com.tealapp.data.model.VideoItem;
import norakomi.com.tealapp.share.ShareVideoTask;

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

    private final String TAG = getClass().getSimpleName();

    private OverviewAdapter adapter;
    private ViewGroup rootView;
    private RecyclerView recycler;
    private ProgressBar progressSpinner;
    private SwipeRefreshLayout swipeRefresh;
    private Disposable disposable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_video_overview, container, false);

        setupViews(rootView);
        getVideos();

        return rootView;
    }

    private void setupViews(ViewGroup rootView) {
        adapter = new OverviewAdapter(this);
        recycler = (RecyclerView) rootView.findViewById(R.id.overview_recycler_video);
        progressSpinner = (ProgressBar) rootView.findViewById(R.id.overview_progress_loader);
        swipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.overview_swiperefresh);

        recycler.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(() -> {
            getVideos();
            swipeRefresh.setRefreshing(false);
        });
    }

    private void getVideos() {
        showProgressSpinner(true);
        setupVideoSubscription();
    }

    private void setupVideoSubscription() {
        // Using Rx here because we could have one result for cached items and one result for api call
        Observable<List<VideoItem>> observable =
                DataManager.getInstance().getVideosRx(YOUTUBE_SEARCH_STRING)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());

        disposable = observable.subscribe(this::onResultGetVideos, this::onErrorGetVideos);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setupVideoSubscription();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        disposable.dispose();
    }

    private void onErrorGetVideos(Throwable throwable) {
        String message = "Error getting videos";
        Logging.logError(TAG, message, throwable);
        UiNotification.showSnackbarLong(getActivity(), getString(R.string.overview_error_loading_videos));
    }

    private void onResultGetVideos(List<VideoItem> videoItems) {
        Logging.log(TAG, "in OnresultsOk. Size video items: " + videoItems.size());
        showProgressSpinner(false);
        adapter.setRecyclerVideos(videoItems);

    }

    private void showProgressSpinner(boolean enable) {
        progressSpinner.setVisibility(enable ? View.VISIBLE : View.GONE);
        recycler.setVisibility(enable ? View.GONE : View.VISIBLE);
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
                boolean bookmarked =  DataManager.getInstance().isVideoBookmarked(videoId);
                Logging.log(TAG, "switchcase Bookmark/ setting bookmark enabled to: " + bookmarked);

                String snackBarText = "You " + (!bookmarked ? "un-" : "") + "bookmarked this video";
                UiNotification.showSnackbarLong(getActivity(), snackBarText);
                break;
            default:
                Logging.logError(TAG, "Unable to handle requested action",
                        new IllegalStateException(requestedAction.name()));
        }
    }
}
