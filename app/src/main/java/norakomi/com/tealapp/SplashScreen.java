package norakomi.com.tealapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Rik van Velzen, Norakomi.com, on 27-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int delayMillis = 1000;
        new Handler().postDelayed(() -> {
            // Start home activity
            startActivity(new Intent(SplashScreen.this, VideoOverviewActivity.class));
            // close splash activity
            finish();
        }, delayMillis);


    }


}
