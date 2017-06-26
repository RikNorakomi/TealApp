package norakomi.com.tealapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

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

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        int delayMillis = 1000;
        new Handler().postDelayed(() -> {
            // Start home activity
            startActivity(new Intent(SplashScreen.this, VideoOverviewActivity.class));
            // close splash activity
            finish();
        }, delayMillis);


    }


}
