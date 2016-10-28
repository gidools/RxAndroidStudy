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

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;

import maxst.com.rxandroidstudy.R;
import rx.Observable;

public class CombinedLatestFragment extends Fragment {

	private static final String TAG = CombinedLatestFragment.class.getSimpleName();

	public CombinedLatestFragment() {
		// Required empty public constructor
	}

	public static CombinedLatestFragment newInstance(String param1, String param2) {
		CombinedLatestFragment fragment = new CombinedLatestFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View layout = inflater.inflate(R.layout.fragment_combine_latest, container, false);

		CheckBox checkBox1 = (CheckBox)layout.findViewById(R.id.checkBox1);
		EditText editText1 = (EditText)layout.findViewById(R.id.editText1);

		Observable<Boolean> checks1 = RxCompoundButton.checkedChanges(checkBox1);
		checks1.subscribe(editText1::setEnabled);

		Observable<Boolean> textExist1 = RxTextView.textChanges(editText1)
				.map(TextUtils::isEmpty)
				.map(empty -> !empty);

		Observable<Boolean> textValidations1 = Observable.combineLatest(checks1, textExist1, (check, exist) -> !check || exist);

		CheckBox checkBox2 = (CheckBox) layout.findViewById(R.id.checkBox2);
		EditText editText2 = (EditText) layout.findViewById(R.id.editText2);

		Observable<Boolean> checks2 = RxCompoundButton.checkedChanges(checkBox2);
		checks2.subscribe(editText2::setEnabled);

		Observable<Boolean> textExist2 = RxTextView.textChanges(editText2)
				.map(TextUtils::isEmpty)
				.map(empty -> !empty);

		Observable<Boolean> textValidations2 = Observable.combineLatest(checks2, textExist2, (check, exist) -> !check || exist);

		CheckBox checkBox3 = (CheckBox) layout.findViewById(R.id.checkBox3);
		EditText editText3 = (EditText) layout.findViewById(R.id.editText3);

		Observable<Boolean> checks3 = RxCompoundButton.checkedChanges(checkBox3);
		checks3.subscribe(editText3::setEnabled);

		Observable<Boolean> textExist3 = RxTextView.textChanges(editText3)
				.map(TextUtils::isEmpty)
				.map(empty -> !empty);

		Observable<Boolean> textValidations3 = Observable.combineLatest(checks3, textExist3, (check, exist) -> !check || exist);

		Button loginBtn = (Button)layout.findViewById(R.id.loginBtn);

//		Observable.combineLatest(textValidations1, textValidations2, textValidations3,
//				(validation1, validation2, validation3) ->
//						validation1 && validation2 && validation3)
//				.subscribe(loginBtn::setEnabled);

		return layout;
	}
}
