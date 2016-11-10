package maxst.com.rxandroidstudy.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import maxst.com.rxandroidstudy.R;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class BGWorkRxSchedulerFragment extends Fragment {

	private static final String TAG = BGWorkRxSchedulerFragment.class.getSimpleName();

	@Bind(R.id.progress_operation_running)
	ProgressBar progressBar;

	@Bind(R.id.list_threading_log)
	ListView logListView;

	private LogAdapter logAdapter;
	private List<String> logList;
	private CompositeSubscription compositeSubscription = new CompositeSubscription();

	public BGWorkRxSchedulerFragment() {
		// Required empty public constructor
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setupLogger();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_bg_work_rx_scheduler, container, false);
		ButterKnife.bind(this, layout);
		return layout;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
		compositeSubscription.clear();
	}

	@OnClick(R.id.btn_start_operation)
	public void startLongOperation() {
		progressBar.setVisibility(View.VISIBLE);
		logList.clear();
		logAdapter.clear();
		addLog("Button Clicked");

		Subscription s = Observable.create(
				subscriber -> {
					addLog("performing long operation");
					subscriber.onNext("onNext");
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						subscriber.onError(new Throwable("Error"));
					}
					subscriber.onCompleted();
				})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(onNext -> {}
						, onError -> {
							addLog(String.format("Boo! Error %s", onError.getMessage()));
							progressBar.setVisibility(View.INVISIBLE);
						}
						, () -> {
							addLog("On complete");
							progressBar.setVisibility(View.INVISIBLE);
						}); // Observer

		compositeSubscription.add(s);
	}

	private void addLog(String logMsg) {
		if (isCurrentlyOnMainThread()) {
			logList.add(0, logMsg + " (main thread) ");
			logAdapter.clear();
			logAdapter.addAll(logList);
		} else {
			logList.add(0, logMsg + " (NOT main thread) ");

			// You can only do below stuff on main thread.
			new Handler(Looper.getMainLooper()).post(() -> {
				logAdapter.clear();
				logAdapter.addAll(logList);
			});
		}
	}

	private void setupLogger() {
		logList = new ArrayList<>();
		logAdapter = new LogAdapter(getActivity(), new ArrayList<>());
		logListView.setAdapter(logAdapter);
	}

	private boolean isCurrentlyOnMainThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}

	private class LogAdapter
			extends ArrayAdapter<String> {

		public LogAdapter(Context context, List<String> logs) {
			super(context, R.layout.item_log, R.id.item_log, logs);
		}
	}
}
