package maxst.com.rxandroidstudy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import maxst.com.rxandroidstudy.R;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

/**
 * Created by Giseok on 2016-10-30.
 */

public class DoubleBindingTextViewFragment extends Fragment {

	@Bind(R.id.double_binding_num1)
	EditText num1EditText;

	@Bind(R.id.double_binding_num2)
	EditText num2EditText;

	@Bind(R.id.double_binding_result)
	TextView resultTextView;

	Subscription subscription;
	PublishSubject<Float> publishSubject;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_double_binding_textview, container, false);
		ButterKnife.bind(this, layout);

		// region -- bug code
//		rx.Observable<TextViewTextChangeEvent> numb1Observable = RxTextView.textChangeEvents(num1EditText)
//				.filter(textChanges -> !TextUtils.isEmpty(textChanges.text().toString()));
//
//		rx.Observable<TextViewTextChangeEvent> numb2Observable = RxTextView.textChangeEvents(num2EditText)
//				.filter(textChanges -> !TextUtils.isEmpty(textChanges.text().toString()));
//
//		Observable.combineLatest(numb1Observable, numb2Observable,
//				(textChangeEvents1, textChangeEvents2) -> {
//					float num1 = Float.parseFloat(textChangeEvents1.text().toString());
//					float num2 = Float.parseFloat(textChangeEvents2.text().toString());
//					return num1 + num2; }) //)
//				.subscribe(result -> resultTextView.setText(String.valueOf(result)));
		// endregion -- bug code

		// region -- bug fix
		rx.Observable<Float> numb1Observable = RxTextView.textChangeEvents(num1EditText)
				.map(textChanges -> {
					float num = 0;
					if (!TextUtils.isEmpty(textChanges.text().toString())) {
						num = Float.parseFloat(textChanges.text().toString());
					}
					return num;
				});

		rx.Observable<Float> numb2Observable = RxTextView.textChangeEvents(num2EditText)
				.map(textChanges -> {
					float num = 0;
					if (!TextUtils.isEmpty(textChanges.text().toString())) {
						num = Float.parseFloat(textChanges.text().toString());
					}
					return num;
				});

		Observable.combineLatest(numb1Observable, numb2Observable,
				(num1, num2) -> {
					return num1 + num2;
				})
				.subscribe(result -> resultTextView.setText(String.valueOf(result)));
		// endregion -- bug fix

		return layout;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		ButterKnife.unbind(this);
	}


}
