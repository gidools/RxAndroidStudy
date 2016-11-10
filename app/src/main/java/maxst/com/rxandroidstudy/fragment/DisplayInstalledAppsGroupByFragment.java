package maxst.com.rxandroidstudy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import maxst.com.rxandroidstudy.R;
import maxst.com.rxandroidstudy.util.AppInfo;
import maxst.com.rxandroidstudy.util.ApplicationAdapter;
import maxst.com.rxandroidstudy.util.ApplicationsList;
import rx.Observable;
import rx.functions.Func2;
import rx.observables.GroupedObservable;

public class DisplayInstalledAppsGroupByFragment extends Fragment {

	private static final String TAG = DisplayInstalledAppsGroupByFragment.class.getSimpleName();

	@Bind(R.id.swipe_container)
	SwipeRefreshLayout swipeRefreshLayout;

	@Bind(R.id.recyclerView)
	RecyclerView recyclerView;

	private ApplicationAdapter applicationAdapter;
	private List<AppInfo> appInfoList = new ArrayList<>();

	public DisplayInstalledAppsGroupByFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_installed_apps_group_by, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ButterKnife.bind(this, view);

		recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

		applicationAdapter = new ApplicationAdapter(new ArrayList<>(), R.layout.applications_list_item);
		recyclerView.setAdapter(applicationAdapter);

		swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.myPrimaryColor));
		swipeRefreshLayout.setProgressViewOffset(false, 0,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

		// Progress
		swipeRefreshLayout.setEnabled(false);
		swipeRefreshLayout.setRefreshing(true);
		recyclerView.setVisibility(View.GONE);

		List<AppInfo> apps = ApplicationsList.getInstance().getList();
		loadApps(apps);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	private void loadApps(List<AppInfo> apps) {
		recyclerView.setVisibility(View.VISIBLE);

		Observable<GroupedObservable<String, AppInfo>> groupedObservableObservable = Observable.from(apps)
				.groupBy(appInfo -> {
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
					return dateFormat.format(new Date(appInfo.getLastUpdateTime()));
				});

		Observable.concat(groupedObservableObservable)
				.subscribe(
						appInfo -> {
							appInfoList.add(appInfo);
							applicationAdapter.addApplication(appInfoList.size() - 1, appInfo);
						}, error -> {
							swipeRefreshLayout.setRefreshing(false);
						}, () -> {
							swipeRefreshLayout.setRefreshing(false);
						});
	}

}
