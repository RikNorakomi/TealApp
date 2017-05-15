package norakomi.com.tealapp.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

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
    private ViewGroup rootView;
    private OverviewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_bookmark, container, false);
        this.rootView = rootView;
        Logging.log(TAG, "in onCreateVIew");



        return rootView;
    }

    @Override
    public void onResume() {
        Logging.log(TAG, "in onResume");
        super.onResume();

        // todo handle race condition// handle bookmark reactively
        List<VideoItem> bookmarkedVideos = DataManager.getInstance().getBookmarkedVideos();
        Logging.log(TAG , "bookmarkedVideos.size = " + bookmarkedVideos.size());

        boolean bookmarkedVideosAvailable = !bookmarkedVideos.isEmpty();

        TextView emptyStateTextView = (TextView) rootView.findViewById(R.id.fragment_bookmark_empty_state_text);
        RecyclerView recycler = (RecyclerView) rootView.findViewById(R.id.fragment_bookmark_recycler);

        adapter = new OverviewAdapter(RequestedActionProvider.getInstance());
        recycler.setAdapter(adapter);
        adapter.setRecyclerVideos(bookmarkedVideos);

        emptyStateTextView.setVisibility(bookmarkedVideosAvailable ? View.GONE : View.VISIBLE);
        recycler.setVisibility(bookmarkedVideosAvailable ? View.VISIBLE : View.GONE);

        Logging.log(TAG , "setting recycler visibility to: " + recycler.getVisibility());

    }

}
