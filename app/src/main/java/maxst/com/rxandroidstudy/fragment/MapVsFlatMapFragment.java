package maxst.com.rxandroidstudy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;

import maxst.com.rxandroidstudy.R;
import rx.Observable;

public class MapVsFlatMapFragment extends Fragment {

	private static final String TAG = MapVsFlatMapFragment.class.getSimpleName();

	public MapVsFlatMapFragment() {
		// Required empty public constructor
	}

	public static MapVsFlatMapFragment newInstance(String param1, String param2) {
		MapVsFlatMapFragment fragment = new MapVsFlatMapFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View layout = inflater.inflate(R.layout.fragment_map_vs_flat_map, container, false);

		Observable.just("item1")
				.map(str -> {
					return str;
				})
				.subscribe(value -> ((TextView) layout.findViewById(R.id.textView1)).setText(value)
						, error -> {
						}
						, () -> Log.i(TAG, "completed"));

		Observable.just("item2")
				.map(str -> {
					String str2 = str + "++";
					String str3 = str + "++++";

					return Observable.just(str, str2, str3);
				})
				.subscribe(value -> ((TextView) layout.findViewById(R.id.textView2)).setText(value.toString())
						, error -> {
						}
						, () -> Log.i(TAG, "completed"));

		Observable.just("item3")
				.flatMap(str -> {
					String str2 = str + "++";
					String str3 = str + "++++";

					return Observable.just(str, str2, str3);
				})
				.subscribe(value -> {
							Log.i(TAG, "onNext : " + value);
							((TextView) layout.findViewById(R.id.textView3)).setText(value);
						}
						, error -> {
						}
						, () -> Log.i(TAG, "completed"));

		Observable.just("item4")
				.flatMap(str -> {
					String str2 = str + "++";
					String str3 = str + "++++";

					return Observable.just(new String[] {str, str2, str3});
				})
				.subscribe(value -> {
							Log.i(TAG, "onNext : " + value.toString());
							((TextView) layout.findViewById(R.id.textView4)).setText(value.toString());
						}
						, error -> {
						}
						, () -> Log.i(TAG, "completed"));

		Observable.just("item5")
				.flatMap(str -> {
					String str2 = str + "++";
					String str3 = str + "++++";

					return Observable.from(new String[] {str, str2, str3});
				})
				.subscribe(value -> {
							Log.i(TAG, "onNext : " + value);
							((TextView) layout.findViewById(R.id.textView5)).setText(value) ;}
						, error -> {
						}
						, () -> Log.i(TAG, "completed"));

		return layout;
	}
}
