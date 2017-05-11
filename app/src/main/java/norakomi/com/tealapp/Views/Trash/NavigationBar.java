package norakomi.com.tealapp.Views.Trash;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import norakomi.com.tealapp.R;

/**
 * Created by Rik van Velzen, Norakomi.com, on 9-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class NavigationBar extends LinearLayout
//        ViewTreeObserver.OnGlobalLayoutListener,
//        View.OnClickListener
 {

    private LinearLayout itemContainer;

    public NavigationBar(Context context) {
        super(context);
        init();
    }

    public NavigationBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(this.getContext()).inflate(R.layout.navigation_bar, this);
        itemContainer = (LinearLayout) this.findViewById(R.id.navigation_bar_container);
        //We wait for the onGlobalLayout call so we know the size of this view, which allows us to layout the navigation items.
//        this.getViewTreeObserver().addOnGlobalLayoutListener(this);
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
