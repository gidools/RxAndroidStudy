package maxst.com.rxandroidstudy.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import maxst.com.rxandroidstudy.R;

public class MainFragment extends Fragment {

	public MainFragment() {
		// Required empty public constructor
	}

	public static MainFragment newInstance(String param1, String param2) {
		MainFragment fragment = new MainFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View layout =  inflater.inflate(R.layout.fragment_main, container, false);
		layout.findViewById(R.id.rx_basic).setOnClickListener(onClickListener);
		layout.findViewById(R.id.rx_lambda).setOnClickListener(onClickListener);
		layout.findViewById(R.id.rx_map).setOnClickListener(onClickListener);
		layout.findViewById(R.id.rx_view_click).setOnClickListener(onClickListener);
		layout.findViewById(R.id.rx_merge).setOnClickListener(onClickListener);
		layout.findViewById(R.id.rx_scan).setOnClickListener(onClickListener);
		layout.findViewById(R.id.rx_scan2).setOnClickListener(onClickListener);
		layout.findViewById(R.id.rx_combined_latest).setOnClickListener(onClickListener);
		layout.findViewById(R.id.rx_combined_latest).setOnClickListener(onClickListener);
		layout.findViewById(R.id.flat_map).setOnClickListener(onClickListener);
		layout.findViewById(R.id.rx_map_vs_flat_map).setOnClickListener(onClickListener);
		layout.findViewById(R.id.async_bg_work).setOnClickListener(onClickListener);
		layout.findViewById(R.id.scheduler_bg_work).setOnClickListener(onClickListener);
		layout.findViewById(R.id.debounce_emitter).setOnClickListener(onClickListener);
		layout.findViewById(R.id.double_binding_text_view).setOnClickListener(onClickListener);
		layout.findViewById(R.id.buffer_demo).setOnClickListener(onClickListener);
		layout.findViewById(R.id.form_validation).setOnClickListener(onClickListener);
		layout.findViewById(R.id.polling).setOnClickListener(onClickListener);
		return layout;
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
				case R.id.rx_basic:
					clickedOn(new ObservableAndSubscriberFragment());
					break;

				case R.id.rx_lambda:
					clickedOn(new LambdaFragment());
					break;

				case R.id.rx_map:
					clickedOn(new MapFragment());
					break;

				case R.id.rx_view_click:
					clickedOn(new ViewClickFragment());
					break;

				case R.id.rx_merge:
					clickedOn(new MergeFragment());
					break;

				case R.id.rx_scan:
					clickedOn(new ScanFragment());
					break;

				case R.id.rx_scan2:
					clickedOn(new Scan2Fragment());
					break;

				case R.id.rx_combined_latest:
					clickedOn(new CombinedLatestFragment());
					break;

				case R.id.rx_map_vs_flat_map:
					clickedOn(new MapVsFlatMapFragment());
					break;

				case R.id.flat_map:
					clickedOn(new FlatMapFragment());
					break;

				case R.id.async_bg_work:
					clickedOn(new AsyncBGWorkFragment());
					break;

				case R.id.scheduler_bg_work:
					clickedOn(new SchedulerBGWorkFragment());
					break;

				case R.id.debounce_emitter:
					clickedOn(new DebounceSearchEmitterFragment());
					break;

				case R.id.double_binding_text_view:
					clickedOn(new DoubleBindingTextViewFragment());
					break;

				case R.id.buffer_demo:
					clickedOn(new BufferDemoFragment());
					break;

				case R.id.form_validation:
					clickedOn(new FormValidationFragment());
					break;

				case R.id.polling:
					clickedOn(new PollingFragment());
					break;
			}
		}
	};

	private void clickedOn(@NonNull Fragment fragment) {
		final String tag = fragment.getClass().toString();
		getActivity().getSupportFragmentManager()
				.beginTransaction()
				.addToBackStack(tag)
				.replace(android.R.id.content, fragment, tag)
				.commit();
	}
}
