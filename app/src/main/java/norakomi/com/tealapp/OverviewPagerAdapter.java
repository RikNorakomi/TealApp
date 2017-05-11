package norakomi.com.tealapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.Views.BookmarkFragment;
import norakomi.com.tealapp.Views.DownloadFragment;
import norakomi.com.tealapp.Views.VideoOverviewFragment;
import norakomi.com.tealapp.Views.WebsiteFragment;

/**
 * Created by Rik van Velzen, Norakomi.com, on 10-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

class OverviewPagerAdapter extends FragmentStatePagerAdapter {

    private final String TAG = getClass().getSimpleName();

    private static final int NUM_PAGES = 4;

    public OverviewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new VideoOverviewFragment();
            case 1:
                return new WebsiteFragment();
            case 2:
                return new BookmarkFragment();
            case 3:
                return new DownloadFragment();
            default:
                String msg = "Unable to determine";
                Logging.logError(TAG, msg, new IllegalStateException(msg));
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
