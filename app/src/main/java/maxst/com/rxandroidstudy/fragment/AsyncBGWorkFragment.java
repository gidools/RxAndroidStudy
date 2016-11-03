package maxst.com.rxandroidstudy.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AsyncBGWorkFragment extends Fragment {

	private static final String TAG = AsyncBGWorkFragment.class.getSimpleName();

	@Bind(R.id.progress_operation_running)
	ProgressBar progressBar;

	@Bind(R.id.list_threading_log)
	ListView logListView;

	private LogAdapter logAdapter;
	private List<String> logList;
	private Handler handler;

	public AsyncBGWorkFragment() {
		// Required empty public constructor
	}

	public static AsyncBGWorkFragment newInstance(String param1, String param2) {
		AsyncBGWorkFragment fragment = new AsyncBGWorkFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setupLogger();

		HandlerThread handlerThread = new HandlerThread("BG Work");
		handlerThread.start();
		handler = new Handler(handlerThread.getLooper());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_async_bg_work, container, false);
		ButterKnife.bind(this, layout);
		return layout;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
		handler.removeCallbacksAndMessages(null);
		handler = null;
	}

	@OnClick(R.id.btn_start_operation)
	public void startLongOperation() {
		progressBar.setVisibility(View.VISIBLE);
		logList.clear();
		logAdapter.clear();
		addLog("Button Clicked");

		handler.post(new Runnable() {
			@Override
			public void run() {
				addLog("Within runnable");
				String result = doSomeLongOperationThatBlocksCurrentThread();

				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						addLog(result);
						progressBar.setVisibility(View.INVISIBLE);
					}
				});
			}
		});
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

	private String doSomeLongOperationThatBlocksCurrentThread() {
		addLog("performing long operation");

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Log.d(TAG, "Operation was interrupted");

			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					addLog("Error occurred!!");
					progressBar.setVisibility(View.INVISIBLE);
				}
			});
		}

		return "BG work completed";
	}

	private class LogAdapter
			extends ArrayAdapter<String> {

		public LogAdapter(Context context, List<String> logs) {
			super(context, R.layout.item_log, R.id.item_log, logs);
		}
	}
}
