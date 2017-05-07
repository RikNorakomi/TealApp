package norakomi.com.tealapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.data.model.VideoItem;

/**
 * Created by Rik van Velzen, Norakomi.com, on 4-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private final IVideoClickedCallback mVideoClickedCallback;

    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    private List<VideoItem> mContent = new ArrayList<>();

    public OverviewAdapter(IVideoClickedCallback callback) {
        mVideoClickedCallback = callback;
    }

    public void setContent(List<VideoItem> content) {
        mContent.clear();
        mContent.addAll(content);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoItem item = mContent.get(position);
        holder.setView(item);

        // Here you apply the animation when the view is bound
//        setAnimation(holder.itemView, position);
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
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


    class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * Info on options menu in recyclerView
         * http://stackoverflow.com/questions/37601346/create-options-menu-for-recyclerview-item
         *
         * Info on: PopupMenu click causing RecyclerView to scroll
         * http://stackoverflow.com/questions/29473977/popupmenu-click-causing-recyclerview-to-scroll
         */

        private final TextView title;
        private final ImageView thumbnail;
        private final TextView menuIcon;
        private VideoItem item;

        private ViewHolder(final View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
            title = (TextView) itemView.findViewById(R.id.video_title);
            menuIcon = (TextView) itemView.findViewById(R.id.menu_icon);
            menuIcon.setOnClickListener(view -> showPopupMenu(view.getContext()));

            thumbnail.setOnClickListener(view -> mVideoClickedCallback.videoClickedWithId(item.getId()));
        }

        public void setView(VideoItem item) {
            this.item = item;
            Glide.with(thumbnail.getContext()).
                    load(item.getThumbnailURL()).
                    centerCrop().
                    into(thumbnail);
            title.setText(item.getTitle());
        }

        private void showPopupMenu(Context context) {
            //creating a popup menu

            PopupMenu popup = new PopupMenu(context, menuIcon);
            //inflating menu from xml resource
            popup.inflate(R.menu.options_menu);
            //adding click listener
            popup.setOnMenuItemClickListener(item1 -> {
                Logging.logError(TAG, "menu item clicked: " + item1.toString());
                switch (item1.getItemId()) {
                    case R.id.navigation_drawer_item1:
                        //handle menu1 click
                        break;
                    case R.id.navigation_drawer_item2:
                        //handle menu2 click
                        break;
                    case R.id.navigation_drawer_item3:
                        //handle menu3 click
                        break;
                }
                return false;
            });
            //displaying the popup
            popup.show();
        }

    }
}
