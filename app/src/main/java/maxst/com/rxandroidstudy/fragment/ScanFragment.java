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
import rx.functions.Func2;

public class ScanFragment extends Fragment {

	private static final String TAG = ScanFragment.class.getSimpleName();

	public ScanFragment() {
		// Required empty public constructor
	}

	public static ScanFragment newInstance(String param1, String param2) {
		ScanFragment fragment = new ScanFragment();
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

//		together.scan(0, new Func2<Integer, Integer, Integer>() {
//			@Override
//			public Integer call(Integer integer, Integer integer2) {
//				return (integer + integer2);
//			}
//		}).subscribe(count -> ((TextView)getView().findViewById(R.id.countText)).setText(count.toString()));;
//
		together.scan(0, (sum, number) -> sum + 1)
				.subscribe(count -> ((TextView)getView().findViewById(R.id.countText)).setText(count.toString()));
	}
}
