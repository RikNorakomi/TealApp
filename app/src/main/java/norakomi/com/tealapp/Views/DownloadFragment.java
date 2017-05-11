package norakomi.com.tealapp.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import norakomi.com.tealapp.R;
import norakomi.com.tealapp.data.DataManager;

/**
 * Created by Rik van Velzen, Norakomi.com, on 10-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class DownloadFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_download, container, false);

        Button buttonLog = (Button) rootView.findViewById(R.id.logBookmarkedVids);
        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataManager.getInstance().getBookmarkedVideos();
            }
        });
        return rootView;
    }
}
