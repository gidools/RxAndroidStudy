package maxst.com.rxandroidstudy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import maxst.com.rxandroidstudy.R;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

public class ObservableAndSubscriberFragment extends Fragment {

	private static final String TAG = ObservableAndSubscriberFragment.class.getSimpleName();

	private Observable<String> delayedObservable;

	public ObservableAndSubscriberFragment() {
		// Required empty public constructor
	}

	public static ObservableAndSubscriberFragment newInstance(String param1, String param2) {
		ObservableAndSubscriberFragment fragment = new ObservableAndSubscriberFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View layout = inflater.inflate(R.layout.fragment_observable_and_subscriber, container, false);
		ButterKnife.bind(this, layout);
		return layout;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		Observable<String> simpleObservable =
				Observable.create(subscriber -> {
					subscriber.onNext("Hello RxAndroid!");
					//subscriber.onError(new Throwable("Error!"));
					subscriber.onCompleted();
				});

//		Observable<String> simpleObservable =
//				Observable.create(new Observable.OnSubscribe<String>() {
//					@Override
//					public void call(Subscriber<? super String> subscriber) {
//						subscriber.onNext("Hello RxAndroid !!");
//						subscriber.onError(new Throwable("Error!!"));
//						subscriber.onCompleted();
//					}
//				});

//		Observable<String> simpleObservable = Observable.just("Hello RxAndroid");

		setSubscriber1(simpleObservable);
		setSubscriber2(simpleObservable);

		delayedObservable = Observable.create(subscriber -> {
			Log.i(TAG, "DelayedObservable is created");
			subscriber.onNext("Hello RxAndroid!");
			//subscriber.onError(new Throwable("Error!"));
			subscriber.onCompleted();
		});
	}

	private void setSubscriber1(Observable<String> observable) {

		observable.subscribe(new Subscriber<String>() {
					@Override
					public void onCompleted() {
						Log.d(TAG, "complete!");
					}

					@Override
					public void onError(Throwable e) {
						Log.e(TAG, "error: " + e.getMessage());
					}

					@Override
					public void onNext(String text) {
						((TextView) getView().findViewById(R.id.textView1)).setText(text);
					}
				});
	}

	private void setSubscriber2(Observable<String> observable) {

		observable.subscribe(new Action1<String>() {
							   @Override
							   public void call(String input) {
								   ((TextView) getView().findViewById(R.id.textView2)).setText("length: " + input.length());
							   }
						   },
						new Action1<Throwable>() {
							@Override
							public void call(Throwable throwable) {
								Log.e(TAG, "error: " + throwable.getMessage());
							}
						}, new Action0() {
							@Override
							public void call() {
								Log.e(TAG, "Completed");
							}
						});
	}

	@OnClick(R.id.subscribe_btn)
	public void startSubscription() {
		delayedObservable.subscribe(newString -> ((TextView) getView().findViewById(R.id.textView3)).setText(newString));
	}
}
