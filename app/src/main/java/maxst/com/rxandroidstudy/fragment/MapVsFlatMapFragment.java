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

//		Observable.just("item1")
//				.map(String::toUpperCase)
//				.subscribe(value -> {
//							Log.i(TAG, "onNext : " + value);
//							((TextView) layout.findViewById(R.id.textView1)).setText(value);
//						}
//						, error -> {
//						}
//						, () -> Log.i(TAG, "item1 completed")
//				);
//
//		Observable.just("item2")
//				.map(str -> {
//					String str2 = str + "++";
//					String str3 = str + "++++";
//
//					return Observable.just(str, str2, str3);
//				})
//				.subscribe(value -> {
//							Log.i(TAG, "item2 onNext : " + value.toString());
//							((TextView) layout.findViewById(R.id.textView2)).setText(value.toString());
//						}
//						, error -> {
//						}
//						, () -> Log.i(TAG, "item2 completed"));
//
//		Observable.just("item3")
//				.map(str -> {
//					String str2 = str + "++";
//					String str3 = str + "++++";
//
//					return Observable.from(new String[]{str, str2, str3});
//				})
//				.subscribe(value -> {
//							Log.i(TAG, "item3 onNext : " + value);
//							((TextView) layout.findViewById(R.id.textView3)).setText(value.toString());
//						}
//				);

//		Observable.from(new String[]{"item4", "item4++", "item4++++"})
//				.scan("", (oldStr, newStr) -> oldStr + newStr + " ")
//				.subscribe(value -> {
//							Log.i(TAG, "item4 onNext : " + value);
//							((TextView) layout.findViewById(R.id.textView4)).setText(value);
//						}
//				);
//
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

//		Observable.just("item7")
//				.flatMap(str -> {
//					String str2 = str + "++";
//					String str3 = str + "++++";
//
//					return Observable.just(new String[]{str, str2, str3});
//				})
//				.map(strArray -> {
//					for (String str : strArray) {
//						stringBuilder.append(str);
//						stringBuilder.append(", ");
//					}
//
//					return stringBuilder.toString();
//				})
//				.subscribe(value -> ((TextView) layout.findViewById(R.id.textView7)).setText(value));
//
//		stringBuilder.delete(0, stringBuilder.length() - 1);

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
