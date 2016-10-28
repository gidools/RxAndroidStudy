package maxst.com.rxandroidstudy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import rx.Observable;

public class RxMergeActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rx_merge);

		Observable<String> lefts = RxView.clicks(findViewById(R.id.leftButton))
				.map(event -> "left");

		Observable<String> rights = RxView.clicks(findViewById(R.id.rightButton))
				.map(event -> "right");

		Observable<String> together = Observable.merge(lefts, rights);

		together.subscribe(text -> ((TextView) findViewById(R.id.textView)).setText(text));

//		together.map(text -> text.toUpperCase())
//				.subscribe(text -> Toast.makeText(this, text, Toast.LENGTH_SHORT).show());

		findViewById(R.id.goto_rx_scan).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(RxMergeActivity.this, RxScanActivity.class));
			}
		});
	}
}
