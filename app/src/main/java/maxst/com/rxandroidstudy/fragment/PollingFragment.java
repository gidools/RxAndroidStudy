package maxst.com.rxandroidstudy.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import maxst.com.rxandroidstudy.R;
import maxst.com.rxandroidstudy.util.LogAdapter;
import rx.Observable;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gidools on 2016-10-31.
 */

public class PollingFragment extends Fragment {

	private static final String TAG = PollingFragment.class.getSimpleName();

	private static final int INITIAL_DELAY = 0;
	private static final int POLLING_INTERVAL = 1000;
	private static final int POLL_COUNT = 8;

	@Bind(R.id.list_threading_log)
	ListView logListView;

	private LogAdapter logAdapter;
	private List<String> logList;
	private CompositeSubscription compositeSubscription;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_polling, container, false);
		ButterKnife.bind(this, layout);

		compositeSubscription = new CompositeSubscription();
		return layout;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setupLog();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		ButterKnife.unbind(this);
		compositeSubscription.unsubscribe();
	}

	@OnClick(R.id.btn_start_simple_polling)
	public void startSimplePolling() {
		int pollingCount = POLL_COUNT;

		compositeSubscription.add(
				Observable.interval(INITIAL_DELAY, POLLING_INTERVAL, TimeUnit.MICROSECONDS)
						.map(this::doNetworkCallAndStringResult)
						.take(pollingCount)
						.doOnSubscribe(() -> {
							addLog(String.format("Start simple polling - %s", count));
						})
						.subscribe(taskName -> {
							addLog(String.format(Locale.US, "Executing polled task [%s] now time : [xx:%02d]",
									taskName, getSecondHand()));
						}));

	}

	@OnClick(R.id.btn_start_increasingly_delayed_polling)
	public void startIncreasingDelayedPolling() {
		setupLog();

		final int pollingInterval = POLLING_INTERVAL;
		final int pollCount = POLL_COUNT;

		addLog(String.format(Locale.US, "Start increasingly delayed polling now time: [xx:%02d]",
				getSecondHand()));

		compositeSubscription.add(//
				Observable.just(1)
						.repeatWhen(new RepeatWithDelay(pollCount, pollingInterval))
						.subscribe(o -> {
							addLog(String.format(Locale.US, "Executing polled task now time : [xx:%02d]",
									getSecondHand()));
						}, e -> {
							Log.d(TAG, "arrrr. Error");
						}, () -> {
							addLog("Completed");
						})
		);
	}

	private int count = 0;

	private String doNetworkCallAndStringResult(long attempt) {
		try {
			if (attempt == 4) {
				Thread.sleep(3000);
			} else {
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		count++;

		return String.valueOf(count);
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

	private int getSecondHand() {
		long millis = System.currentTimeMillis();
		return (int) (TimeUnit.MILLISECONDS.toSeconds(millis) -
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}

	//public static class RepeatWithDelay
	public class RepeatWithDelay
			implements Func1<Observable<? extends Void>, Observable<?>> {

		private final int _repeatLimit;
		private final int _pollingInterval;
		private int _repeatCount = 1;

		RepeatWithDelay(int repeatLimit, int pollingInterval) {
			_pollingInterval = pollingInterval;
			_repeatLimit = repeatLimit;
		}

		// this is a notificationhandler, all we care about is
		// the emission "type" not emission "content"
		// only onNext triggers a re-subscription

		@Override
		public Observable<?> call(Observable<? extends Void> inputObservable) {

			// it is critical to use inputObservable in the chain for the result
			// ignoring it and doing your own thing will break the sequence

			return inputObservable.flatMap(new Func1<Void, Observable<?>>() {
				@Override
				public Observable<?> call(Void blah) {


					if (_repeatCount >= _repeatLimit) {
						// terminate the sequence cause we reached the limit
						addLog("Completing sequence");
						return Observable.empty();
					}

					// since we don't get an input
					// we store state in this handler to tell us the point of time we're firing
					_repeatCount++;

					return Observable.timer(_repeatCount * _pollingInterval,
							TimeUnit.MILLISECONDS);
				}
			});
		}
	}
}
