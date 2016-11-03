package maxst.com.rxandroidstudy;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import maxst.com.rxandroidstudy.fragment.MainFragment;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(android.R.id.content, new MainFragment(), this.toString())
					.commit();
		}
	}
}
