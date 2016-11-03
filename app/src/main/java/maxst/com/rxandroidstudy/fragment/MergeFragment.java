package maxst.com.rxandroidstudy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import maxst.com.rxandroidstudy.R;
import rx.Observable;

public class MergeFragment extends Fragment {

	private static final String TAG = MergeFragment.class.getSimpleName();

	public MergeFragment() {
		// Required empty public constructor
	}

	public static MergeFragment newInstance(String param1, String param2) {
		MergeFragment fragment = new MergeFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_view_click, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();

		Observable<String> lefts = RxView.clicks(getView().findViewById(R.id.leftButton))
				.map(event -> "left");

		Observable<String> rights = RxView.clicks(getView().findViewById(R.id.rightButton))
				.map(event -> "right");

		Observable<String> together = Observable.merge(lefts, rights);

		together.subscribe(text -> ((TextView) getView().findViewById(R.id.textView)).setText(text));
	}
}
