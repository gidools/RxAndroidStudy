package maxst.com.rxandroidstudy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

import maxst.com.rxandroidstudy.R;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

public class LambdaFragment extends Fragment {

	private static final String TAG = LambdaFragment.class.getSimpleName();

	public LambdaFragment() {
		// Required empty public constructor
	}

	public static LambdaFragment newInstance(String param1, String param2) {
		LambdaFragment fragment = new LambdaFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_lambda, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();

		Observable<String> simpleObservable = Observable.just("Hello RxAndroid");

		setSubscriber1(simpleObservable);
	}

	private void setSubscriber1(Observable<String> observable) {

		observable
				.map(event -> new Random().nextInt())
				.subscribe(value -> {
							Log.e(TAG, "subscribe. onNext");
							TextView textView = (TextView) getView().findViewById(R.id.textView1);
							textView.setText("Random Number : " + value.toString());
						}, error -> {
							Log.e(TAG, "subscribe. Error: " + error.getMessage());
							error.printStackTrace();
						}
						, () -> {
							// Completed
						}
				);
	}
}
