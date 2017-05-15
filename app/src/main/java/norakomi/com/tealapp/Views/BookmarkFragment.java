package norakomi.com.tealapp.Views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import norakomi.com.tealapp.OverviewAdapter;
import norakomi.com.tealapp.R;
import norakomi.com.tealapp.RequestedActionProvider;
import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.data.DataManager;
import norakomi.com.tealapp.data.model.VideoItem;

/**
 * Created by Rik van Velzen, Norakomi.com, on 10-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class BookmarkFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private OverviewAdapter adapter;
    private Disposable disposable;
    private TextView emptyStateTextView;
    private RecyclerView recycler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_bookmark, container, false);
        emptyStateTextView = (TextView) rootView.findViewById(R.id.fragment_bookmark_empty_state_text);
        recycler = (RecyclerView) rootView.findViewById(R.id.fragment_bookmark_recycler);

        adapter = new OverviewAdapter(RequestedActionProvider.getInstance());
        recycler.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Using Rx here because we could have one result for cached items and one result for api call
        Observable<List<VideoItem>> observable =
                DataManager.getInstance().getBookmarkedVideosRx()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
        disposable = observable.subscribe(this::onResultGetBookmarkedVideos, this::onErrorGetBookmarkedVideos);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        disposable.dispose();
    }

    private void onResultGetBookmarkedVideos(List<VideoItem> videoItems) {
        Logging.log(TAG, "!!in onResultGetBookmarkedVideos. size = " + videoItems.size());
        boolean bookmarkedVideosAvailable = !videoItems.isEmpty();

        emptyStateTextView.setVisibility(bookmarkedVideosAvailable ? View.GONE : View.VISIBLE);
        recycler.setVisibility(bookmarkedVideosAvailable ? View.VISIBLE : View.GONE);
        adapter.setRecyclerVideos(videoItems);
    }

    private void onErrorGetBookmarkedVideos(Throwable throwable) {
        String message = "onErrorGetBookmarkedVideos";
        Logging.logError(TAG, message, throwable);

    }


}
