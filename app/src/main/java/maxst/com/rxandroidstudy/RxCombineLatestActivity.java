package maxst.com.rxandroidstudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;

import rx.Observable;

public class RxCombineLatestActivity extends AppCompatActivity {

	private static final String TAG = RxCombineLatestActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rx_combine_latest);

		CheckBox checkBox1 = (CheckBox)findViewById(R.id.checkBox1);
		EditText editText1 = (EditText)findViewById(R.id.editText1);

		Observable<Boolean> checks1 = RxCompoundButton.checkedChanges(checkBox1);
//		checks1.subscribe(check -> {
//			Log.i(TAG, "Check changed : " + check);
//			editText1.setEnabled(check);
//		});

		Observable<Boolean> textExists1 = RxTextView.textChanges(editText1)
				.map(RxCombineLatestActivity::isEmpty);

		Observable<Boolean> textValidations1 = Observable.combineLatest(checks1, textExists1, (check, exist) -> !check || exist);

		CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
		EditText editText2 = (EditText) findViewById(R.id.editText2);

		Observable<Boolean> checks2 = RxCompoundButton.checkedChanges(checkBox2);
//		checks2.subscribe(check -> editText2.setEnabled(check));

		Observable<Boolean> textExist2 = RxTextView.textChanges(editText2)
				.map(RxCombineLatestActivity::isEmpty);

		Observable<Boolean> textValidations2 = Observable.combineLatest(checks2, textExist2, (check, exist) -> !check || exist);

		CheckBox checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
		EditText editText3 = (EditText) findViewById(R.id.editText3);

		Observable<Boolean> checks3 = RxCompoundButton.checkedChanges(checkBox3);
//		checks3.subscribe(check -> editText3.setEnabled(check));

		Observable<Boolean> textExists3 = RxTextView.textChanges(editText3)
				.map(RxCombineLatestActivity::isEmpty);

		Observable<Boolean> textValidations3 = Observable.combineLatest(checks3, textExists3, (check, exist) -> !check || exist);

		Button loginBtn = (Button)findViewById(R.id.loginBtn);

		Observable.combineLatest(textValidations1, textValidations2, textValidations3,
				(validation1, validation2, validation3) ->
						validation1 && validation2 && validation3)
				.subscribe(validation -> loginBtn.setEnabled(validation));
	}

	private static boolean isEmpty(CharSequence sequence) {
		return sequence.length() != 0;
	}
}
