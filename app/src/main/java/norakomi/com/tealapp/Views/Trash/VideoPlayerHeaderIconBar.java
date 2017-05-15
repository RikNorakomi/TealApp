package norakomi.com.tealapp.Views.Trash;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import norakomi.com.tealapp.R;
import norakomi.com.tealapp.Utils.Logging;

/**
 * Created by Rik van Velzen, Norakomi.com, on 9-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class VideoPlayerHeaderIconBar extends LinearLayout
//        ViewTreeObserver.OnGlobalLayoutListener,
//        View.OnClickListener
{

    private final String TAG = getClass().getSimpleName();

    private LinearLayout itemContainer;
    private IconClickListener clickListener;

    private View mCommentsIconContainer;
    private View mThumbsDownIconContainer;
    private View mThumbsUpIconContainer;
    private View mShareIconContainer;
    private View mBookmarkIconContainer;

    public VideoPlayerHeaderIconBar(Context context) {
        super(context);
        init();
    }

    public VideoPlayerHeaderIconBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }



    public enum Icons {
        COMMENTS(R.id.comments_icon, R.id.comments_text),
        THUMBS_UP(R.id.thumb_up_icon, R.id.thumb_up_text),
        THUMBS_DOWN(R.id.thumb_down_icon, R.id.thumb_down_text),
        BOOKMARK(R.id.bookmark_icon, R.id.bookmark_text),
        SHARE(R.id.share_icon, R.id.share_text);

        private final int iconResourceId;
        private final int textResourceId;

        Icons(@IdRes int iconResourceId, @IdRes int textResourceId) {
            this.iconResourceId = iconResourceId;
            this.textResourceId = textResourceId;
        }

        public int getIconResourceId() {
            return iconResourceId;
        }

        public int getTextResourceId() {
            return textResourceId;
        }
    }

    public interface IconClickListener {
        void onIconClicked(@NonNull Icons clickedIcon);
    }

    private void init() {
        LayoutInflater.from(this.getContext()).inflate(R.layout.recycler_header_icons_toolbar, this);

        // view references
        itemContainer = (LinearLayout) this.findViewById(R.id.header_icon_bar_root_view);
        mCommentsIconContainer = this.findViewById(R.id.comments_container);
        mThumbsDownIconContainer = this.findViewById(R.id.thumb_down_container);
        mThumbsUpIconContainer = this.findViewById(R.id.thumb_up_container);
        mBookmarkIconContainer = this.findViewById(R.id.bookmark_container);
        mShareIconContainer = this.findViewById(R.id.share_container);

        setClickListeners();

        //We wait for the onGlobalLayout call so we know the size of this view, which allows us to layout the navigation items.
//        this.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    boolean bookmarkEnabled = false;

    private void setClickListeners() {
        mCommentsIconContainer.setOnClickListener(view -> handleClick(Icons.COMMENTS));
        mThumbsDownIconContainer.setOnClickListener(view -> handleClick(Icons.THUMBS_DOWN));
        mThumbsUpIconContainer.setOnClickListener(view -> handleClick(Icons.THUMBS_UP));
        mBookmarkIconContainer.setOnClickListener(view -> {
            handleClick(Icons.BOOKMARK);
            bookmarkEnabled = !bookmarkEnabled;
            setBookmarkEnabled(bookmarkEnabled);

        });
        mShareIconContainer.setOnClickListener(view -> handleClick(Icons.SHARE));
    }

    private void handleClick(Icons icon) {
        if (clickListener != null) {
            clickListener.onIconClicked(icon);
        } else {
            // clickListener not available!
            String msg = "No icon clickListener registered. Please setClickListner via .setIconClickListener()";
            Logging.logError(TAG, msg, new NullPointerException(msg));
        }
    }

    public void setIconClickListener(IconClickListener clickListener) {
        // create weak reference to prevent potential memory leaks
        this.clickListener = clickListener;
        Logging.log(TAG , "IconClickLIstener registered!");
    }

    public void setBookmarkEnabled(boolean enabled) {
        setColorOnIcon(R.id.bookmark_icon, enabled);
        setColorOnText(R.id.bookmark_text, enabled);
    }

    public void setThumbedUpEnabled(boolean enabled) {
        setColorOnIcon(R.id.thumb_up_icon, enabled);
        setColorOnText(R.id.thumb_up_text, enabled);
    }

    public void setThumbedDownEnabled(boolean enabled) {
        setColorOnIcon(R.id.thumb_down_icon, enabled);
        setColorOnText(R.id.thumb_down_text, enabled);
    }

    public void selectIcon(Icons icon, boolean enable) {
        setColorOnIcon(icon.getIconResourceId(), enable);
        setColorOnText(icon.getTextResourceId(), enable);
    }

    private void setColorOnIcon(@IdRes int iconResourceId, boolean enabled) {
        int color = getResources().getColor(enabled ? R.color.fillColorIconsHeaderSelected : R.color.fillColorIconsHeaderUnselected);
        ((ImageView) findViewById(iconResourceId)).setColorFilter(color);
    }

    private void setColorOnText(@IdRes int TextViewResourceId, boolean enabled) {
        int color = getResources().getColor(enabled ? R.color.fillColorIconsHeaderSelected : R.color.fillColorIconsHeaderUnselected);
        ((TextView) findViewById(TextViewResourceId)).setTextColor(color);
    }

    public void setThumbUpEnabled(boolean enabled) {

    }

    public void setThumbDownEnabled(boolean enabled) {

    }


    //    @Override
    public void onClick(View view) {

    }

//    @Override
//    public void onGlobalLayout() {
//        if (this.getContext() != null) {
//            AndroidVersionSpecific.removeGlobalLayoutListener(this, this);
//        }
//    }

}
