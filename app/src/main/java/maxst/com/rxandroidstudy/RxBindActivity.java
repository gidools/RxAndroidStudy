package maxst.com.rxandroidstudy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.Random;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

public class RxBindActivity extends AppCompatActivity {

	private static final String TAG = RxBindActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rx_bind);

		Observable<Object> viewObservable = RxView.clicks(findViewById(R.id.btn_click));

		// Only Last subscriber can be given observable's event!
//		setClickListener1(viewObservable);
		setClickListener2(viewObservable);
		setClickListener1(viewObservable);

		findViewById(R.id.goto_rx_merge).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(RxBindActivity.this, RxMergeActivity.class));
			}
		});
	}

	private void setClickListener1(Observable<Object> viewObservable) {
		viewObservable
				.map(new Func1<Object, Integer>() {
					@Override
					public Integer call(Object o) {
						return new Random().nextInt();
					}
				})
				.subscribe(new Subscriber<Integer>() {
					@Override
					public void onCompleted() {
						Log.e(TAG, "subscribe1. onCompleted");
					}

					@Override
					public void onError(Throwable e) {
						Log.e(TAG, "subscribe1. Error: " + e.getMessage());
						e.printStackTrace();
					}

					@Override
					public void onNext(Integer integer) {
						Log.e(TAG, "subscribe1. onNext");
						TextView textView = (TextView) findViewById(R.id.textView1);
						textView.setText("number 1: " + integer.toString());
					}
				});

	}

	private void setClickListener2(Observable<Object> viewObservable) {
		viewObservable
				.map(event -> new Random().nextInt())
				.subscribe(value -> {
							Log.e(TAG, "subscribe2. onNext");
							TextView textView = (TextView) findViewById(R.id.textView2);
							textView.setText("number 2: " + value.toString());
						}, error -> {
							Log.e(TAG, "subscribe2. Error: " + error.getMessage());
							error.printStackTrace();
						}
						, () -> {
							// Completed
						}
				);
	}
}
