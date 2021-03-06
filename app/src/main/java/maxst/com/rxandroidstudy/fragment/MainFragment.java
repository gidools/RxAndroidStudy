package maxst.com.rxandroidstudy.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
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
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		ButterKnife.unbind(this);
	}

	@OnClick(R.id.rx_basic)
	public void startBasic() {
		clickedOn(new ObservableAndSubscriberFragment());
	}

	@OnClick(R.id.rx_lambda)
	public void lambdaExample() {
		clickedOn(new LambdaFragment());
	}

	@OnClick(R.id.rx_map)
	public void mapExample() {
		clickedOn(new MapFragment());
	}

	@OnClick(R.id.rx_view_click)
	public void viewClickExample() {
		clickedOn(new ViewClickFragment());
	}

	@OnClick(R.id.rx_merge)
	public void merge() {
		clickedOn(new MergeFragment());
	}

	@OnClick(R.id.rx_scan)
	public void scan() {
		clickedOn(new ScanFragment());
	}

	@OnClick(R.id.rx_combined_latest)
	public void combinedLatest() {
		clickedOn(new CombinedLatestFragment());
	}

	@OnClick(R.id.rx_flat_map)
	public void mapVsFlatMap() {
		clickedOn(new FlatMapFragment());
	}

	@OnClick(R.id.async_http)
	public void flatMap() {
		clickedOn(new AsyncHttpFragment());
	}

	@OnClick(R.id.synchronous_http)
	public void synchronousHttp() {
		clickedOn(new SynchronousHttpFragment());
	}

	@OnClick(R.id.bg_work_thread)
	public void bgWorkThread() {
		clickedOn(new BGWorkThreadFragment());
	}

	@OnClick(R.id.bg_work_async_task)
	public void bgWorkAsyncTask() {
		clickedOn(new BGWorkAsyncTaskFragment());
	}

	@OnClick(R.id.bg_work_rx_scheduler)
	public void scheduler() {
		clickedOn(new BGWorkRxSchedulerFragment());
	}

	@OnClick(R.id.debounce_emitter)
	public void debounce() {
		clickedOn(new DebounceSearchEmitterFragment());
	}

	@OnClick(R.id.double_binding_text_view)
	public void doubleBinding() {
		clickedOn(new DoubleBindingTextViewFragment());
	}

	@OnClick(R.id.buffer_demo)
	public void buffer() {
		clickedOn(new BufferDemoFragment());
	}

	@OnClick(R.id.form_validation)
	public void formValidation() {
		clickedOn(new FormValidationFragment());
	}

	@OnClick(R.id.polling)
	public void polling() {
		clickedOn(new PollingFragment());
	}

	@OnClick(R.id.display_bitmap)
	public void displayBitmap() {
		clickedOn(new DisplayBitmapFragment());
	}

	@OnClick(R.id.display_installed_apps)
	public void displayInstalledApps() {
		clickedOn(new DisplayInstalledAppsFragment());
	}

	@OnClick(R.id.display_installed_apps2)
	public void displayInstalledApps2() {
		clickedOn(new DisplayInstalledAppsFragment2());
	}

	@OnClick(R.id.display_installed_apps_repeat)
	public void displayInstalledAppsRepeat() {
		clickedOn(new DisplayInstalledAppsRepeatFragment());
	}

	@OnClick(R.id.display_installed_apps_scan)
	public void displayInstalledAppsScan() {
		clickedOn(new DisplayInstalledAppsScanFragment());
	}

	@OnClick(R.id.display_installed_apps_group_by)
	public void displayInstalledAppsGroupBy() {
		clickedOn(new DisplayInstalledAppsGroupByFragment());
	}

	@OnClick(R.id.network_download)
	public void networkDownload() {
		clickedOn(new NetworkDownloadFragment());
	}

	private void clickedOn(@NonNull Fragment fragment) {
		final String tag = fragment.getClass().toString();
		getActivity().getSupportFragmentManager()
				.beginTransaction()
				.addToBackStack(tag)
				.replace(android.R.id.content, fragment, tag)
				.commit();
	}
}
