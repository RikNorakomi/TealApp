package norakomi.com.tealapp.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rik van Velzen, Norakomi.com, on 13-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class WeakReferenceList<T> {

    /**
     * Class to help listener registration with defensive weakreference to not have memory issues of kept references to ui components
     * Every method is synchronized, to make sure the item list is not corrupted during adding/removing
     */

    private static final String TAG = WeakReferenceList.class.getSimpleName();

    private final ArrayList<WeakReference<T>> items = new ArrayList<>();

    /**
     * adds an item, double calls with the same item will result in only 1 item added!
     *
     * @param item item to add
     */

    public synchronized void add(T item) {
        if (item == null) {
            return;
        }

        // check if it is already listed, double listing gives headaches we want to avoid :)
        // WeakReference does not implement equals :( so do hard core checking
        for (WeakReference<T> weakReference : items) {
            T obj = weakReference.get();
            if (item == obj) {
                //object is already listed
                return;
            }
        }

        //ok it is not in the list yet, add it
        items.add(new WeakReference<>(item));
    }

    public synchronized void clear() {
        items.clear();
    }

    public synchronized void remove(T item) {
        if (items.size() == 0) {
            return;
        }
        //make defensive copy to protect against concurrent modification exception while removing items
        final List<WeakReference<T>> itemList = new ArrayList<>(items);
        boolean removed = false;
        for (WeakReference<T> weakReference : itemList) {
            T obj = weakReference.get();
            if (obj == null) {
                //clean up null reference while we are busy
                items.remove(weakReference);
                App.log(TAG, "removed null reference!");
            } else if (item == obj) {
                items.remove(weakReference);
                removed = true;
            }
        }
        if (!removed) {
            String message = "could not remove item! new size= " + items.size();
            App.logError(TAG, message, new Exception(message));
        }
    }

    /**
     * @return list of guarantueed non-null items, use this to iterate over the items or to check if items are present (isEmpty())
     */
    public synchronized List<T> getItems() {
        final List<T> itemList = new ArrayList<>();
        if (!items.isEmpty()) {
            for (WeakReference<T> weakReference : items) {
                T item = weakReference.get();
                if (item != null) {
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }
}
