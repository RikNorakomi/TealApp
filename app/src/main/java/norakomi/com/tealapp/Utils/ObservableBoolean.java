package norakomi.com.tealapp.Utils;

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

public class ObservableBoolean {
    private boolean value;
    private PublishSubject<Boolean> subject = PublishSubject.create();

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
        subject.onNext(value);
    }

    public Observable<Boolean> getObservable() {
        return subject;
    }
}
