package norakomi.com.tealapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.util.Date;

import norakomi.com.tealapp.Utils.App;

/**
 * Created by Rik van Velzen, on 30-10-2016.
 * <p/>
 * This activity takes care of querying the youtube api with a search keyword
 * More info can be found at:
 * https://developers.google.com/youtube/v3/docs/search
 * https://developers.google.com/youtube/v3/docs/search/list
 */

public class VideoOverviewActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private long backButtonElapsedTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_youtube);
        setupViews();
    }

    private void setupViews() {
        // remove shadow line from toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
        }

        OverviewPagerAdapter mPagerAdapter = new OverviewPagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.overview_viewpager);
        pager.setAdapter(mPagerAdapter);
        pager.setOffscreenPageLimit(3);

        setupTabLayout(pager);
    }

    private void setupTabLayout(ViewPager pager) {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.overview_tab_layout);
        tabLayout.setupWithViewPager(pager);

        // add icons to tab layout
        int[] imageResId = {
                R.drawable.ic_videocam_black_24dp,
                R.drawable.ic_web_black_24dp,
                R.drawable.ic_collections_bookmark_black_24dp,
                R.drawable.ic_cloud_download_black_24dp
        };

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(imageResId[i]);
            }
        }
    }


    @Override
    public void onBackPressed() {
        long timestamp = new Date().getTime();

        /* App exit can only be performed by double clickin gon back within specified millisecs. */
        if (timestamp - backButtonElapsedTime <= 5000) {
            backButtonElapsedTime = 0;
            super.onBackPressed();
        } else {
            backButtonElapsedTime = timestamp;
            App.toast("Press again to exit app");
        }
    }
}

