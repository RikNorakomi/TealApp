package norakomi.com.tealapp.share;

import android.content.Context;
import android.content.Intent;

import norakomi.com.tealapp.data.model.VideoItem;

/**
 * Created by Rik van Velzen, Norakomi.com, on 9-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class ShareVideoTask {

    public static final String YOUTUBE_BASE_SHARE_URL = "https://www.youtube.com/watch?v=";
    private final Context context;


    public ShareVideoTask(VideoItem video, Context context) {
        this.context = context;
        shareVideoLink(video);
    }

    private void shareVideoLink(VideoItem video) {
        String subject = "Watch \"" + video.getTitle() + "\" on YouTube";
        String shareBody = YOUTUBE_BASE_SHARE_URL + video.getId();

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,shareBody);

        context.startActivity(Intent.createChooser(sharingIntent, "Share video link"));
    }

//    private void share(Uri uri) {
//        /** Sharing to Facebook only shares the image and not the text. In order to share text you need to
//         *  use the Facebook SDK */
//
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.setType("image/jpg");
//        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Art work from Norakomi's Soviet Poster Art app");
//        shareIntent.putExtra(Intent.EXTRA_TEXT,
//                Html.fromHtml("<p>I found this art work via the <a href=" + Constants.TEMP_APP_URL + ">"
//                        + Constants.APP_NAME + " App</a>:</p>" +
//                        "Title: " + artwork.getTitle() + "<br>" +
//                        "Author: " + artwork.getAuthor() + "<br>" +
//                        "Date: " + artwork.getYear() + "<br>" ));
//
//        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        context.startActivity(Intent.createChooser(shareIntent, "Share Soviet Poster Artwork:"));
//    }
}
