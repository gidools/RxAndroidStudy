package maxst.com.rxandroidstudy.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import maxst.com.rxandroidstudy.R;
import maxst.com.rxandroidstudy.util.LogAdapter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Gidools on 2016-10-31.
 */

public class BufferDemoFragment extends Fragment {

	private static final String TAG = BufferDemoFragment.class.getSimpleName();

	@Bind(R.id.list_threading_log)
	ListView logListView;

	@Bind(R.id.btn_start_operation)
	Button startBtn;

	private Subscription subscription;
	private List<String> logList;
	private LogAdapter logAdapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_buffer, container, false);
		ButterKnife.bind(this, layout);
		return layout;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setupLog();
	}

	@Override
	public void onResume() {
		super.onResume();

		subscription = RxView.clickEvents(startBtn)
				.map(click -> {
					addLog("Button clicked");
					return 1;
				})
				.buffer(1, TimeUnit.SECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(onNext -> {
					if (onNext.size() > 0) {
						addLog("Button click count : " + onNext.size());
					} else {
						Log.i(TAG, "No click");
					}
				});
	}

	@Override
	public void onPause() {
		super.onPause();

		subscription.unsubscribe();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		ButterKnife.unbind(this);
	}

	private void setupLog() {
		logList = new ArrayList<>();
		logAdapter = new LogAdapter(getActivity(), new ArrayList<String>());
		logListView.setAdapter(logAdapter);
	}

	private void addLog(String logMsg) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			logList.add(0, logMsg + " [Main thread]");
			logAdapter.clear();
			logAdapter.addAll(logList);
		} else {
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					logList.add(0, logMsg + " [Not main thread]");
					logAdapter.clear();
					logAdapter.addAll(logList);
				}
			});
		}
	}
}
