package norakomi.com.tealapp.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rik van Velzen, on 30-10-2016.
 */
public class VideoItem extends RealmObject {

    @PrimaryKey
    public String id;
    private String title;
    private String description;
    private String thumbnailURL;

    private boolean isBookmarked;
    private boolean isThumbedUp;

    private boolean isThumbedDown;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnail) {
        this.thumbnailURL = thumbnail;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public boolean isThumbedUp() {
        return isThumbedUp;
    }

    public void setThumbedUp(boolean thumbedUp) {
        isThumbedUp = thumbedUp;
    }

    public boolean isThumbedDown() {

        return isThumbedDown;
    }

    public void setThumbedDown(boolean thumbedDown) {
        isThumbedDown = thumbedDown;
    }

    @Override
    public String toString() {
        return "VideoItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", thumbnailURL='" + thumbnailURL + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
