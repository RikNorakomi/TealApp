package norakomi.com.tealapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

    private List<VideoItem> mContent = new ArrayList<>();

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
//            description = (TextView) itemView.findViewById(R.id.video_description);
            menuIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(view.getContext());
                }
            });
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startVideoPlayerActivity(view.getContext());
                }
            });
        }

        public void setView(VideoItem item) {
            this.item = item;
            Glide.with(thumbnail.getContext()).
                    load(item.getThumbnailURL()).
                    centerCrop().
                    into(thumbnail);
            title.setText(item.getTitle());
//            description.setText(item.getDescription());
        }

        private void showPopupMenu(Context context) {
            //creating a popup menu

            PopupMenu popup = new PopupMenu(context, menuIcon);
            //inflating menu from xml resource
            popup.inflate(R.menu.options_menu);
            //adding click listener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Logging.logError(TAG, "menu item clicked: " + item.toString());
                    switch (item.getItemId()) {
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
                }
            });
            //displaying the popup
            popup.show();
        }

        private void startVideoPlayerActivity(Context context) {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("VIDEO_ID", item.getId());
            context.startActivity(intent);
        }
    }
}
