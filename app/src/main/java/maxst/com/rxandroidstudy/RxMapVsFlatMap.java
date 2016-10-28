package maxst.com.rxandroidstudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import rx.Observable;

public class RxMapVsFlatMap extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rx_map_vs_flat_map);

		Observable.just("item1")
				.map(str -> { return str;})
				.subscribe(value -> Log.i("RxMapVsFlatMap", "Result of Map: " + value)
						,error -> {}
						,() -> Log.i("RxMapVsFlatMap", "completed"));

		Observable.just("item2")
				.flatMap(str -> {
					String str2 = str + "++";
					String str3 = str + "++++";

					return Observable.just(str, str2, str3);
				})
				.subscribe(value -> Log.i("RxMapVsFlatMap", "Result of FlatMap : " + value),
						error -> {},
						() -> Log.i("RxMapVsFlatMap", "completed"));

		Observable.just("item3")
				.flatMap(str -> {
					String str2 = str + "++";
					String str3 = str + "++++";

					return Observable.just(new String[] {str, str2, str3});
				})
				.subscribe(value -> Log.i("RxMapVsFlatMap", "Result of FlatMap : " + value.toString()),
						error -> {},
						() -> Log.i("RxMapVsFlatMap", "completed"));

		Observable.just("item4")
				.flatMap(str -> {
					String str2 = str + "++";
					String str3 = str + "++++";

					return Observable.from(new String[] {str, str2, str3});
				})
				.subscribe(value -> Log.i("RxMapVsFlatMap", "Result of FlatMap : " + value),
						error -> {},
						() -> Log.i("RxMapVsFlatMap", "completed"));
	}
}
