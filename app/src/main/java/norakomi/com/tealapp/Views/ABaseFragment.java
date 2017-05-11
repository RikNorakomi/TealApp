package norakomi.com.tealapp.Views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import norakomi.com.tealapp.R;

/**
 * Created by Rik van Velzen, Norakomi.com, on 10-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public abstract class ABaseFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int layout = R.layout.fragment_video_overview;
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                layout, container, false);

        return rootView;
    }

    public abstract int getLayoutId();
}
