package maxst.com.rxandroidstudy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import maxst.com.rxandroidstudy.R;
import rx.Observable;

public class FlatMapFragment extends Fragment {

	private static final String TAG = FlatMapFragment.class.getSimpleName();

	public FlatMapFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View layout = inflater.inflate(R.layout.fragment_flat_map, container, false);

		StringBuilder stringBuilder = new StringBuilder();

		Observable.just("item5")
				.flatMap(str -> {
					String str2 = str + "++";
					String str3 = str + "++++";

					return Observable.just(str, str2, str3);
				})
				.subscribe(
						value -> {
							stringBuilder.append(value);
							stringBuilder.append(", ");
						}, error -> {
						}, () -> {
							((TextView) layout.findViewById(R.id.textView5)).setText(stringBuilder.toString());
						});

		stringBuilder.delete(0, stringBuilder.length() - 1);

		Observable.just("item6")
				.flatMap(str -> {
					String str2 = str + "++";
					String str3 = str + "++++";

					return Observable.just(new String[]{str, str2, str3});
				})
				.map(strArray -> {
					for (String str : strArray) {
						stringBuilder.append(str);
						stringBuilder.append(", ");
					}

					return stringBuilder.toString();
				})
				.subscribe(value -> ((TextView) layout.findViewById(R.id.textView6)).setText(value));

		stringBuilder.delete(0, stringBuilder.length() - 1);

		Observable.just("item8")
				.flatMap(str -> {
					String str2 = str + "++";
					String str3 = str + "++++";

					return Observable.from(new String[]{str, str2, str3});
				})
				.subscribe(
						value -> {
							stringBuilder.append(value);
							stringBuilder.append(", ");
						}, error -> {
						}, () -> ((TextView) layout.findViewById(R.id.textView8)).setText(stringBuilder.toString()));

		return layout;
	}
}
