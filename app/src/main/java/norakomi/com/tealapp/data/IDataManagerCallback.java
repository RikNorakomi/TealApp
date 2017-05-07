package norakomi.com.tealapp.data;

import java.util.List;

import norakomi.com.tealapp.data.model.VideoItem;

/**
 * Created by Rik van Velzen, Norakomi.com, on 7-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public interface IDataManagerCallback {

    void onResult(List<VideoItem> result);

    void onError(Exception e);
}
