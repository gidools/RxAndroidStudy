package maxst.com.rxandroidstudy.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import maxst.com.rxandroidstudy.R;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static java.lang.String.format;

public class DebounceSearchEmitterFragment extends Fragment {

	private static final String TAG = DebounceSearchEmitterFragment.class.getSimpleName();

	@Bind (R.id.list_threading_log)
	ListView logListView;

	@Bind (R.id.input_txt_debounce)
	EditText inputSearchText;

	private LogAdapter logAdapter;
	private List<String> logList;

	private Subscription subscription;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_debounce_search_emitter, container, false);
		ButterKnife.bind(this, layout);
		return layout;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setupLog();
		subscription = RxTextView.textChangeEvents(inputSearchText)
				.debounce(400, TimeUnit.MILLISECONDS)
				.filter(changes -> !TextUtils.isEmpty(changes.text().toString()))
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(changeEvent -> addLog(format("Searching for %s", changeEvent.text().toString()))
						, error -> addLog("Error. check your log")
						, () -> addLog("Complete"));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		subscription.unsubscribe();
		ButterKnife.unbind(this);
	}

	@OnClick (R.id.clr_debounce)
	public void onClearLog() {
		logList.clear();
		logAdapter.clear();
	}

	private void setupLog() {
		logList = new ArrayList<>();
		logAdapter = new LogAdapter(getActivity(), new ArrayList<>());
		logListView.setAdapter(logAdapter);
	}

	private void addLog(String logMsg) {
		Log.i(TAG, "add log : " + logMsg);
		if (Looper.myLooper() == Looper.getMainLooper()) {
			logList.add(0, logMsg + " (main thread) ");
			logAdapter.clear();
			logAdapter.addAll(logList);
		} else {
			logList.add(0, logMsg + " (NOT main thread) ");
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					logAdapter.clear();
					logAdapter.addAll(logList);
				}
			});
		}
	}

	private class LogAdapter extends ArrayAdapter<String> {

		public LogAdapter(Context context, List<String> logList) {
			super(context, R.layout.item_log, R.id.item_log, logList);

		}
	}
}
