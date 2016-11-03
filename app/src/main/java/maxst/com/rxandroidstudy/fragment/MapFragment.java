package maxst.com.rxandroidstudy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import maxst.com.rxandroidstudy.R;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class MapFragment extends Fragment {

	private static final String TAG = MapFragment.class.getSimpleName();

	public MapFragment() {
		// Required empty public constructor
	}

	public static MapFragment newInstance(String param1, String param2) {
		MapFragment fragment = new MapFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_map, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();

		Observable<String> simpleObservable =
				Observable.create(new Observable.OnSubscribe<String>() {
					@Override
					public void call(Subscriber<? super String> subscriber) {
						subscriber.onNext("Hello RxAndroid !!");
//						subscriber.onError(new Throwable("Error!!"));
						subscriber.onCompleted();
					}
				});

//		Observable<String> simpleObservable = Observable.just("Hello RxAndroid");

		setSubscriber(simpleObservable);
	}

	private void setSubscriber(Observable<String> observable) {

		observable
				.map(new Func1<String, String>() {
					@Override
					public String call(String text) {
						return text.toUpperCase();
					}
				})
				.subscribe(new Subscriber<String>() {
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
						((TextView) getView().findViewById(R.id.textView)).setText(text);
					}
				});
	}
}
