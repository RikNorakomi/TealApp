package norakomi.com.tealapp;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
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

import norakomi.com.tealapp.Interfaces.IActionRequestedListener;
import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.Utils.SharedPrefs;
import norakomi.com.tealapp.data.model.VideoItem;

import static norakomi.com.tealapp.Interfaces.IActionRequestedListener.RequestedAction.NOT_INTERESTED;
import static norakomi.com.tealapp.Interfaces.IActionRequestedListener.RequestedAction.PLAY_VIDEO;
import static norakomi.com.tealapp.Interfaces.IActionRequestedListener.RequestedAction.SAVE_TO_WATCH_LATER;
import static norakomi.com.tealapp.Interfaces.IActionRequestedListener.RequestedAction.SHARE;
import static norakomi.com.tealapp.Interfaces.IActionRequestedListener.RequestedAction.SHOW_COMMENTS;
import static norakomi.com.tealapp.Interfaces.IActionRequestedListener.RequestedAction.THUMB_DOWN;
import static norakomi.com.tealapp.Interfaces.IActionRequestedListener.RequestedAction.THUMB_UP;
import static norakomi.com.tealapp.Interfaces.IActionRequestedListener.RequestedAction.TOGGLE_AUTOPLAY;
import static norakomi.com.tealapp.Interfaces.IActionRequestedListener.RequestedAction.TOGGLE_SHOW_DESCRIPTION;

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
    private final IActionRequestedListener mActionListener;
    private final boolean mAddHeaderToRecycler;

    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    private List<VideoItem> mContent = new ArrayList<>();

    private static final int HEADER = 1;
    private static final int VIDEO_ITEM = 2;

    public OverviewAdapter(IActionRequestedListener listener, boolean addHeaderToRecycler) {
        mActionListener = listener;
        mAddHeaderToRecycler = addHeaderToRecycler;
    }

    public void setContent(List<VideoItem> content) {
        mContent.clear();
        mContent.addAll(content);
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
                View header = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_header, null);
                return new ViewHolderHeader(header);
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
        Logging.log(TAG, "viewtype for pos: " + position + " = " + holder.getItemViewType());
        switch (holder.getItemViewType()) {
            case HEADER:
                Logging.log(TAG, "in onBindViewHolder.ViewHolderHeader");
                // do nothing
                break;
            case VIDEO_ITEM:
                Logging.log(TAG, "in onBindViewHolder.ViewHolderItem");
                VideoItem item = mContent.get(position);
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
        return mContent.size();
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

            PopupMenu popup = new PopupMenu(context, menuIcon);
            //inflating menu from xml resource
            popup.inflate(R.menu.options_menu);
            //adding click listener
            popup.setOnMenuItemClickListener(menuItem -> {
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
                    default:
                        Logging.logError(TAG, "Unable to handle requested action",
                                new IllegalStateException(menuItem.toString()));
                }
                return false;
            });
            //displaying the popup
            popup.show();
        }

    }


    class ViewHolderHeader extends RecyclerView.ViewHolder {

        private final SwitchCompat mSwitchCompat;
        private final View mCommentsIcon;
        private final View mThumbsDownIcon;
        private final View mThumbsUpIcon;
        private final View mShareIcon;

        private boolean descriptionCollapsed;
        int rotationAngle = 0;

        ViewHolderHeader(View headerView) {
            super(headerView);
            Logging.log(TAG, "in constructor ViewHolderHeader");
            // view references
            mCommentsIcon = headerView.findViewById(R.id.comments_container);
            mThumbsDownIcon = headerView.findViewById(R.id.thumb_down_container);
            mThumbsUpIcon = headerView.findViewById(R.id.thumb_up_container);
            mShareIcon = headerView.findViewById(R.id.share_container);
            mSwitchCompat = (SwitchCompat) headerView.findViewById(R.id.autoplay_switch);

            mSwitchCompat.setChecked(SharedPrefs.getInstance().isAutoplayEnabled());

            View mTitleContainer = headerView.findViewById(R.id.title_container);
            final View v = headerView.findViewById(R.id.expand_collapse_icon);

            mTitleContainer.setOnClickListener(view -> {
                int targetRotationAngle = descriptionCollapsed ? rotationAngle + 180 : rotationAngle - 180;
                ObjectAnimator anim = ObjectAnimator.ofFloat(v, "rotation", rotationAngle, targetRotationAngle);
                anim.setDuration(500);
                anim.start();
                rotationAngle += 180;
                descriptionCollapsed = !descriptionCollapsed;
            });

            // setup clickListeners
            mSwitchCompat.setOnClickListener(view -> mActionListener.onActionRequested(null, TOGGLE_AUTOPLAY));
            mTitleContainer.setOnClickListener(view -> mActionListener.onActionRequested(null, TOGGLE_SHOW_DESCRIPTION));
            mCommentsIcon.setOnClickListener(view -> mActionListener.onActionRequested(null, SHOW_COMMENTS));
            mThumbsDownIcon.setOnClickListener(view -> mActionListener.onActionRequested(null, THUMB_DOWN));
            mThumbsUpIcon.setOnClickListener(view -> mActionListener.onActionRequested(null, THUMB_UP));
            mShareIcon.setOnClickListener(view -> mActionListener.onActionRequested(null, SHARE));
        }
    }

}
