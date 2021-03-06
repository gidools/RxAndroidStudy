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

public class BGWorkThreadFragment extends Fragment {

	private static final String TAG = BGWorkThreadFragment.class.getSimpleName();

	@Bind(R.id.progress_operation_running)
	ProgressBar progressBar;

	@Bind(R.id.list_threading_log)
	ListView logListView;

	private LogAdapter logAdapter;
	private List<String> logList;
	private Handler handler;

	public BGWorkThreadFragment() {
		// Required empty public constructor
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
		View layout = inflater.inflate(R.layout.fragment_bg_work_thread, container, false);
		ButterKnife.bind(this, layout);
		return layout;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
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

		handler.post(() -> {
				addLog("performing long operation");

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					getActivity().runOnUiThread(() -> {
							addLog("Error occurred!!");
							progressBar.setVisibility(View.INVISIBLE);
						});
				}

				// TODO : Null pointer exception when close fragment before thread completion
				getActivity().runOnUiThread(() -> {
						addLog("Thread completed");
						progressBar.setVisibility(View.INVISIBLE);
					});
			}
		);
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
