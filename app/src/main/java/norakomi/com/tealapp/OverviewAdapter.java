package norakomi.com.tealapp;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import norakomi.com.tealapp.Interfaces.IRequestedActionListener;
import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.Utils.SharedPrefs;
import norakomi.com.tealapp.Views.Trash.VideoPlayerHeaderIconBar;
import norakomi.com.tealapp.data.DataManager;
import norakomi.com.tealapp.data.model.VideoItem;

import static norakomi.com.tealapp.Interfaces.IRequestedActionListener.RequestedAction.BOOKMARK;
import static norakomi.com.tealapp.Interfaces.IRequestedActionListener.RequestedAction.NOT_INTERESTED;
import static norakomi.com.tealapp.Interfaces.IRequestedActionListener.RequestedAction.PLAY_VIDEO;
import static norakomi.com.tealapp.Interfaces.IRequestedActionListener.RequestedAction.SAVE_TO_WATCH_LATER;
import static norakomi.com.tealapp.Interfaces.IRequestedActionListener.RequestedAction.SHARE;
import static norakomi.com.tealapp.Interfaces.IRequestedActionListener.RequestedAction.SHOW_COMMENTS;
import static norakomi.com.tealapp.Interfaces.IRequestedActionListener.RequestedAction.TOGGLE_AUTOPLAY;
import static norakomi.com.tealapp.Interfaces.IRequestedActionListener.RequestedAction.TOGGLE_SHOW_DESCRIPTION;

/**
 * Created by Rik van Velzen, Norakomi.com, on 4-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class OverviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private final IRequestedActionListener mActionListener;
    private final boolean mAddHeaderToRecycler;


    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    private List<VideoItem> mRecyclerVideos = new ArrayList<>();
    private VideoItem mHeaderVideo;

    private static final int HEADER = 1;
    private static final int VIDEO_ITEM = 2;

    /**
     * invoking this constructor causes a recyclerView WITHOUT header to be shown (usage overview activity)
     *
     * @param listener
     */
    public OverviewAdapter(@NonNull IRequestedActionListener listener) {
        mActionListener = listener;
        mAddHeaderToRecycler = false;
    }

    /**
     * invoking this constructor causes a recyclerView WITH header to be shown (usage player activity)
     *
     * @param listener
     */
    public OverviewAdapter(@NonNull IRequestedActionListener listener,
                           @NonNull VideoItem headerVideo) {
        mActionListener = listener;
        mHeaderVideo = headerVideo;
        Logging.log(TAG, "in Constructor OverviewAdapter. mHeaderVideo = " + mHeaderVideo);
        mAddHeaderToRecycler = true;
    }

    public void setRecyclerVideos(List<VideoItem> recyclerVideos) {
        mRecyclerVideos.clear();
        mRecyclerVideos.addAll(recyclerVideos);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 && mAddHeaderToRecycler ? HEADER : VIDEO_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                Logging.log(TAG, "creating viewholder for Header. mHeaderVideo = " + mHeaderVideo);
                View header = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_header, null);
                return new ViewHolderHeader(header, mHeaderVideo);
            case VIDEO_ITEM:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_video_item, null);
                return new ViewHolderItem(view);
            default:
                Logging.logError(TAG, "Unable to determine which view to create", new IllegalStateException());
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case HEADER:
                // do nothing
                break;
            case VIDEO_ITEM:
                VideoItem item = mRecyclerVideos.get(position);
                ((ViewHolderItem) holder).setView(item);
                break;
            default:
                Logging.logError(TAG, "Unable to determine which viewHolder to bind", new IllegalStateException());
        }

        // Here you apply the animation when the view is bound
//        setAnimation(holder.itemView, position);
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.
                    loadAnimation(viewToAnimate.getContext(),
                            android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    @Override
    public int getItemCount() {
        return mRecyclerVideos.size();
    }

    class ViewHolderItem extends RecyclerView.ViewHolder {
        /**
         * Info on options menu in recyclerView
         * http://stackoverflow.com/questions/37601346/create-options-menu-for-recyclerview-item
         * <p>
         * Info on: PopupMenu click causing RecyclerView to scroll
         * http://stackoverflow.com/questions/29473977/popupmenu-click-causing-recyclerview-to-scroll
         */

        private final TextView title;
        private final ImageView thumbnail;
        private final TextView menuIcon;
        private String videoId;
        private VideoItem item;


        private ViewHolderItem(final View itemView) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
            title = (TextView) itemView.findViewById(R.id.video_title);
            menuIcon = (TextView) itemView.findViewById(R.id.menu_icon);

            menuIcon.setOnClickListener(view -> showPopupMenu(view.getContext()));
            thumbnail.setOnClickListener(view -> mActionListener.onActionRequested(videoId, PLAY_VIDEO));
        }

        public void setView(VideoItem item) {
            this.item = item;
            this.videoId = item.getId();
            Glide.with(thumbnail.getContext()).
                    load(item.getThumbnailURL()).
                    centerCrop().
                    into(thumbnail);
            title.setText(item.getTitle());
        }

        public void showPopupMenu(Context context) {
            //creating a popup menu

            PopupMenu popupMenu = new PopupMenu(context, menuIcon);
            //inflating menu from xml resource
            popupMenu.inflate(R.menu.options_menu);

            final boolean isVideoBookmarked = DataManager.getInstance().isVideoBookmarked(videoId);
            MenuItem bookmarkItem = popupMenu.getMenu().findItem(R.id.options_menu_bookmark);
            bookmarkItem.setTitle(isVideoBookmarked ? "Un-bookmark" : "Bookmark");

            //adding click listener
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                Logging.logError(TAG, "menu item clicked: " + menuItem.toString());
                switch (menuItem.getItemId()) {
                    case R.id.options_menu_not_interested:
                        mActionListener.onActionRequested(videoId, NOT_INTERESTED);
                        break;
                    case R.id.options_menu_watch_later:
                        mActionListener.onActionRequested(videoId, SAVE_TO_WATCH_LATER);
                        break;
                    case R.id.options_menu_share:
                        mActionListener.onActionRequested(videoId, SHARE);
                        break;
                    case R.id.options_menu_bookmark:
                        mActionListener.onActionRequested(videoId, BOOKMARK);
//                        isVideoBookmarked = !isVideoBookmarked;
                        bookmarkItem.setTitle(!isVideoBookmarked ? "Un-bookmark" : "Bookmark");
                        break;
                    default:
                        Logging.logError(TAG, "Unable to handle requested action",
                                new IllegalStateException(menuItem.toString()));
                }
                return false;
            });
            //displaying the popup
            popupMenu.show();
        }

    }


    class ViewHolderHeader extends RecyclerView.ViewHolder {

        private final SwitchCompat mSwitchCompat;
        private final TextView mHeaderTitle;
        private final VideoPlayerHeaderIconBar mIconsToolbar;


        private boolean descriptionCollapsed;
        int rotationAngle = 0;

        ViewHolderHeader(View headerView, VideoItem video) {
            super(headerView);
            // view references
            mSwitchCompat = (SwitchCompat) headerView.findViewById(R.id.autoplay_switch);
            mHeaderTitle = (TextView) headerView.findViewById(R.id.header_title_text);
            mIconsToolbar = (norakomi.com.tealapp.Views.Trash.VideoPlayerHeaderIconBar) headerView.findViewById(R.id.icons_toolbar);

            final DataManager dataManager = DataManager.getInstance();
            final String videoId = video.getId();

            mHeaderTitle.setText(video.getTitle());
            mSwitchCompat.setChecked(SharedPrefs.getInstance().isAutoplayEnabled());

            View mTitleContainer = headerView.findViewById(R.id.title_container);
            final View v = headerView.findViewById(R.id.header_expand_collapse_icon);

            mTitleContainer.setOnClickListener(view -> {
                int targetRotationAngle = descriptionCollapsed ? rotationAngle + 180 : rotationAngle - 180;
                ObjectAnimator anim = ObjectAnimator.ofFloat(v, "rotation", rotationAngle, targetRotationAngle);
                anim.setDuration(500);
                anim.start();
                rotationAngle += 180;
                descriptionCollapsed = !descriptionCollapsed;
            });


            // set icon states
            boolean isBookmarked = dataManager.isVideoBookmarked(videoId);
            boolean isThumbedUp = dataManager.isVideoThumbedUp(videoId);
            boolean isThumbedDown = dataManager.isVideoThumbedDown(videoId);
            mIconsToolbar.setBookmarkEnabled(isBookmarked);
            mIconsToolbar.setThumbUpEnabled(isThumbedUp);
            mIconsToolbar.setThumbDownEnabled(isThumbedDown);

            // setup clickListeners
            mSwitchCompat.setOnClickListener(view -> mActionListener.onActionRequested(null, TOGGLE_AUTOPLAY));
            mTitleContainer.setOnClickListener(view -> mActionListener.onActionRequested(null, TOGGLE_SHOW_DESCRIPTION));

            mIconsToolbar.setIconClickListener(clickedIcon -> {
                switch (clickedIcon) {
                    case BOOKMARK:
//                        mActionListener.onActionRequested(null, BOOKMARK);
                        Logging.log(TAG, "switchcase Bookmark/ toogle bookmarked video");
                        dataManager.toggleBookmarkedVideo(videoId);

                        boolean bookmarked = dataManager.isVideoBookmarked(videoId);
                        Logging.log(TAG, "switchcase Bookmark/ setting bookmark enabled to: " + bookmarked);
                        mIconsToolbar.setBookmarkEnabled(bookmarked);
                        break;
                    case COMMENTS:
                        mActionListener.onActionRequested(null, SHOW_COMMENTS);
                        break;
                    case SHARE:
                        mActionListener.onActionRequested(videoId, SHARE);
                        break;
                    case THUMBS_DOWN:
                        // TODO: 16-5-2017 find a better readable pattern here
                        dataManager.toggleThumbDownVideo(videoId);
                        boolean thumbedDown = dataManager.isVideoThumbedDown(videoId);
                        mIconsToolbar.setThumbDownEnabled(thumbedDown);
                        if (thumbedDown) {
                            // check if thumbed up was enabled and if so disable it
                            boolean thumbedUp = dataManager.isVideoThumbedUp(videoId);
                            if (thumbedUp) {
                                dataManager.toggleThumbUpVideo(videoId);
                                mIconsToolbar.setThumbUpEnabled(false);
                            }
                        }
                        break;
                    case THUMBS_UP:
                        // TODO: 16-5-2017 find a better readable pattern here
                        dataManager.toggleThumbUpVideo(videoId);
                        boolean thumbedUp = dataManager.isVideoThumbedUp(videoId);
                        mIconsToolbar.setThumbUpEnabled(thumbedUp);
                        if (thumbedUp) {
                            // check if thumbed up was enabled and if so disable it
                            boolean thumbDown = dataManager.isVideoThumbedDown(videoId);
                            if (thumbDown) {
                                dataManager.toggleThumbDownVideo(videoId);
                                mIconsToolbar.setThumbDownEnabled(false);
                            }
                        }
                        break;
                    default:
                        String message = "Unable to determine valid switch case";
                        Logging.logError(TAG, message, new IllegalStateException(message));
                }
            });
        }
    }

}
