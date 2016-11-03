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

public class Scan2Fragment extends Fragment {

	private static final String TAG = Scan2Fragment.class.getSimpleName();

	public Scan2Fragment() {
		// Required empty public constructor
	}

	public static Scan2Fragment newInstance(String param1, String param2) {
		Scan2Fragment fragment = new Scan2Fragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_scan, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();

		Observable<Integer> minus = RxView.clicks(getView().findViewById(R.id.minusBtn))
				.map(event -> -1);

		Observable<Integer> plus = RxView.clicks(getView().findViewById(R.id.plusBtn))
				.map(event -> 1);

		Observable<Integer> together = Observable.merge(minus, plus);

		together.scan(0, (sum, number) -> sum + number)
				.subscribe(number -> ((TextView)getView().findViewById(R.id.numberText)).setText(number.toString()));
	}
}
