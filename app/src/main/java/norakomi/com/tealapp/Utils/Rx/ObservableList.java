package norakomi.com.tealapp.Utils.Rx;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Rik van Velzen, Norakomi.com, on 16-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class ObservableList<T> {
    private List<T> list;
    private PublishSubject<T> subject = PublishSubject.create();

    public ObservableList() {
        list = new ArrayList<>();
    }

    public void setValue(T value) {
        list.add(value);
        subject.onNext((T) list);
    }

    public Observable<T> getObservable() {
        return subject;
    }

}
