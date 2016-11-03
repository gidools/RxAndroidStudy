package maxst.com.rxandroidstudy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import maxst.com.rxandroidstudy.R;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class ViewClickFragment extends Fragment {

	private static final String TAG = ViewClickFragment.class.getSimpleName();
	private CompositeSubscription compositeSubscription;

	public ViewClickFragment() {
		// Required empty public constructor
	}

	public static ViewClickFragment newInstance(String param1, String param2) {
		ViewClickFragment fragment = new ViewClickFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		compositeSubscription = new CompositeSubscription();
		return inflater.inflate(R.layout.fragment_view_click, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();

		compositeSubscription.add(RxView.clicks(getView().findViewById(R.id.leftButton))
				.map(event -> "left")
				.subscribe(new Action1<String>() {
					@Override
					public void call(String s) {
						((TextView) getView().findViewById(R.id.textView)).setText(s);
					}
				}));

		compositeSubscription.add(RxView.clicks(getView().findViewById(R.id.rightButton))
				.map(event -> "right")
				.subscribe(new Action1<String>() {
					@Override
					public void call(String s) {
						((TextView) getView().findViewById(R.id.textView)).setText(s);
					}
				}));
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		compositeSubscription.unsubscribe();
	}
}
