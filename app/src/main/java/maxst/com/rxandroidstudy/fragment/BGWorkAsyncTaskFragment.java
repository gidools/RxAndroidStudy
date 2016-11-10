package maxst.com.rxandroidstudy.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import maxst.com.rxandroidstudy.R;

public class BGWorkAsyncTaskFragment extends Fragment {

	private static final String TAG = BGWorkAsyncTaskFragment.class.getSimpleName();

	@Bind(R.id.progress_operation_running)
	ProgressBar progressBar;

	@Bind(R.id.list_threading_log)
	ListView logListView;

	private LogAdapter logAdapter;
	private List<String> logList;
	private AsyncTask asyncTask;

	public BGWorkAsyncTaskFragment() {
		// Required empty public constructor
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setupLogger();

		asyncTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... voids) {
				addLog("performing long operation");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO : Null pointer exception when close fragment before thread completion
					getActivity().runOnUiThread(() -> {
								addLog("Error occurred!!");
								progressBar.setVisibility(View.INVISIBLE);
							});
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				addLog("Thread completed");
				// TODO : Null pointer exception when close fragment before thread completion
				progressBar.setVisibility(View.INVISIBLE);
			}
		};

//		asyncTask = new AsyncTaskWorker(getActivity(), this, progressBar);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_bg_work_async_task, container, false);
		ButterKnife.bind(this, layout);
		return layout;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@OnClick(R.id.btn_start_operation)
	public void startLongOperation() {
		progressBar.setVisibility(View.VISIBLE);
		logList.clear();
		logAdapter.clear();
		addLog("Button Clicked");

		asyncTask.execute();
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

	private static class AsyncTaskWorker extends AsyncTask {

		WeakReference<View> viewWeakReference;
		WeakReference<Activity> activityWeakReference;
		WeakReference<BGWorkAsyncTaskFragment> bgWorkAsyncTaskFragmentWeakReference;

		AsyncTaskWorker(Activity activity, BGWorkAsyncTaskFragment fragment, View view) {
			activityWeakReference = new WeakReference<>(activity);
			bgWorkAsyncTaskFragmentWeakReference = new WeakReference<>(fragment);
			viewWeakReference = new WeakReference<>(view);
		}

		@Override
		protected Void doInBackground(Object[] params) {
			BGWorkAsyncTaskFragment fragment = bgWorkAsyncTaskFragmentWeakReference.get();
			Activity activity = activityWeakReference.get();
			View view = viewWeakReference.get();

			if (fragment == null || activity == null || view == null) {
				return null;
			}

			fragment.addLog("performing long operation");

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				activity.runOnUiThread(() -> {
					fragment.addLog("Error occurred!!");
					view.setVisibility(View.INVISIBLE);
				});
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object aVoid) {
			BGWorkAsyncTaskFragment fragment = bgWorkAsyncTaskFragmentWeakReference.get();
			View view = viewWeakReference.get();

			if (fragment == null || view == null) {
				return;
			}

			fragment.addLog("Thread completed");
			view.setVisibility(View.INVISIBLE);
		}
	};
}
