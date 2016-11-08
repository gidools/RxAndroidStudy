package maxst.com.rxandroidstudy.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import maxst.com.rxandroidstudy.R;
import maxst.com.rxandroidstudy.util.AppInfo;
import maxst.com.rxandroidstudy.util.AppInfoRich;
import maxst.com.rxandroidstudy.util.ApplicationAdapter;
import maxst.com.rxandroidstudy.util.ApplicationsList;
import maxst.com.rxandroidstudy.util.Utils;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DisplayInstalledAppsFragment extends Fragment {

	private static final String TAG = DisplayInstalledAppsFragment.class.getSimpleName();

	@Bind(R.id.swipe_container)
	SwipeRefreshLayout swipeRefreshLayout;

	@Bind(R.id.recyclerView)
	RecyclerView recyclerView;

	private ApplicationAdapter applicationAdapter;

	public DisplayInstalledAppsFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_installed_apps, container, false);
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
		swipeRefreshLayout.setOnRefreshListener(this::refreshTheList);
		recyclerView.setVisibility(View.GONE);

		getFileDir()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(file -> {
					refreshTheList();
				});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	private Observable<File> getFileDir() {
		return Observable.create(subscriber -> {
			subscriber.onNext(getContext().getFilesDir());
			subscriber.onCompleted();
		});
	}

	private Observable<AppInfo> getApps() {
		return Observable.create(subscriber -> {
			List<AppInfoRich> apps = new ArrayList<>();
			final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			List<ResolveInfo> infos = getActivity()
					.getPackageManager()
					.queryIntentActivities(mainIntent, 0);
			for (ResolveInfo info : infos) {
				apps.add(new AppInfoRich(getActivity(), info));
			}

			String filesDir = getContext().getFilesDir().getAbsolutePath();

			for (AppInfoRich appInfoRich : apps) {
				Bitmap icon = Utils.drawableToBitmap(appInfoRich.getIcon());
				String name = appInfoRich.getName();
				String iconPath = filesDir + "/" + name;
				Utils.storeBitmap(getContext(), icon, name);

				if (subscriber.isUnsubscribed()) {
					return;
				}

				subscriber.onNext(new AppInfo(name, iconPath, appInfoRich.getLastUpdateTime()));
			}

			if (!subscriber.isUnsubscribed()) {
				subscriber.onCompleted();
			}
		});
	}

	private void refreshTheList() {
		getApps().toSortedList()
				.subscribe(new Observer<List<AppInfo>>() {

					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onNext(List<AppInfo> appInfos) {
						recyclerView.setVisibility(View.VISIBLE);
						applicationAdapter.addApplications(appInfos);
						swipeRefreshLayout.setRefreshing(false);
						storeList(appInfos);
					}
				});
	}

	private void storeList(List<AppInfo> appInfos) {
		ApplicationsList.getInstance().setList(appInfos);

		Schedulers.io().createWorker().schedule(() -> {
			SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
			Type appInfoType = new TypeToken<List<AppInfo>>() {
			}.getType();
			sharedPref.edit().putString("APPS", new Gson().toJson(appInfos, appInfoType)).apply();
		});
	}

}
