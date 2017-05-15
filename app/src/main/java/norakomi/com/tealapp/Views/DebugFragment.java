package norakomi.com.tealapp.Views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import norakomi.com.tealapp.R;
import norakomi.com.tealapp.Utils.Logging;
import norakomi.com.tealapp.data.DataManager;

/**
 * Created by Rik van Velzen, Norakomi.com, on 10-5-2017.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class DebugFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    @NonNull
    public CompositeDisposable mCompositeDisposable;
    private TextView logView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_download, container, false);

        Button buttonLog = (Button) rootView.findViewById(R.id.logBookmarkedVids);
        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataManager.getInstance().getBookmarkedVideos();
            }
        });


        logView = (TextView) rootView.findViewById(R.id.listViewLogcat);
        logView.setMovementMethod(new ScrollingMovementMethod());

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logging.log(TAG , "Creating log subscription");
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(Logging.getLogOutputRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateLogView));
    }

    private void updateLogView(String logOutput){
        String updatedText = logOutput + "\n\n" + logView.getText()  ;
        logView.setText(updatedText);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logging.log(TAG , "in onDetach. Unsubcribed!!!");
        mCompositeDisposable.clear();
    }
}
