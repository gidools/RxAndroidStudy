package maxst.com.rxandroidstudy;

import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import rx.Observable;

public class RxScanActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rx_scan);

		Observable<Integer> minus = RxView.clicks(findViewById(R.id.minusBtn))
				.map(event -> -1);

		Observable<Integer> plus = RxView.clicks(findViewById(R.id.plusBtn))
				.map(event -> 1);

		Observable<Integer> together = Observable.merge(minus, plus);

		together.scan(0, (sum, number) -> sum + 1)
				.subscribe(count -> ((TextView)findViewById(R.id.countText)).setText(count.toString()));

		together.scan(0, (sum, number) -> sum + number)
				.subscribe(number -> ((TextView)findViewById(R.id.numberText)).setText(number.toString()));

		findViewById(R.id.goto_rx_combine_latest).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(RxScanActivity.this, RxCombineLatestActivity.class));
			}
		});

	}
}
