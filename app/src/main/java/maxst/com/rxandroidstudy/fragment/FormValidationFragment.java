package maxst.com.rxandroidstudy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import maxst.com.rxandroidstudy.R;
import rx.Observable;
import rx.Subscription;
import static android.text.TextUtils.isEmpty;
import static android.util.Patterns.EMAIL_ADDRESS;

/**
 * Created by Gidools on 2016-10-31.
 */

public class FormValidationFragment extends Fragment {

	private static final String TAG = FormValidationFragment.class.getSimpleName();

	@Bind(R.id.btn_demo_form_valid)
	Button btnValidIndicator;

	@Bind(R.id.demo_combl_email)
	EditText email;

	@Bind(R.id.demo_combl_password)
	EditText password;

	@Bind(R.id.demo_combl_num)
	EditText number;

	private Observable<CharSequence> emailChangeObservable;
	private Observable<CharSequence> passwordChangeObservable;
	private Observable<CharSequence> numberChangeObservable;

	private Subscription subscription;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_form_validation_comb_latest, container, false);
		ButterKnife.bind(this, layout);

		emailChangeObservable = RxTextView.textChanges(email).skip(1);
		passwordChangeObservable = RxTextView.textChanges(password).skip(1);
		numberChangeObservable = RxTextView.textChanges(number).skip(1);

		combineLatestEvents();
		return layout;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
		subscription.unsubscribe();
	}

	private void combineLatestEvents() {
		subscription = Observable.combineLatest(emailChangeObservable, passwordChangeObservable, numberChangeObservable,
				(newEmail, newPwd, newNumber) -> {
					boolean emailValid = !isEmpty(newEmail) && EMAIL_ADDRESS.matcher(newEmail).matches();
					if (!emailValid) {
						email.setError("Invalid email!");
					}

					boolean pwdValid = !isEmpty(newPwd) && newPwd.length() > 8;
					if (!pwdValid) {
						password.setError("Invalid pwd!");
					}

					boolean numberValid = !isEmpty(newNumber);
					if (numberValid) {
						int num = Integer.parseInt(newNumber.toString());
						numberValid = num > 0 && num <= 100;
					}

					if (!numberValid) {
						number.setError("Invalid number!");
					}
					return emailValid && pwdValid && numberValid;
				})
				.subscribe(formValid -> {
					btnValidIndicator.setEnabled(formValid);
				});
	}
}
