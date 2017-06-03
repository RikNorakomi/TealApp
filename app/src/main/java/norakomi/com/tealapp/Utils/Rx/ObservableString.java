package norakomi.com.tealapp.Utils.Rx;

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

public class ObservableString {
    private String value;
    private PublishSubject<String> subject = PublishSubject.create();

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        subject.onNext(value);
    }

    public Observable<String> getObservable() {
        return subject;
    }
}
