package maxst.com.rxandroidstudy.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import maxst.com.rxandroidstudy.R;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class NetworkDownloadFragment extends Fragment {

	private static final String TAG = NetworkDownloadFragment.class.getSimpleName();

	@Bind(R.id.arc_progress)
	ArcProgress arcProgress;

	@Bind(R.id.button_download)
	Button downloadBtn;

	private PublishSubject<Integer> downloadProgress = PublishSubject.create();

	public NetworkDownloadFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_network_download, container, false);
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

	@OnClick(R.id.button_download)
	void downloadFile() {
		downloadBtn.setText("Downloading..");
		downloadBtn.setClickable(false);

		downloadProgress = PublishSubject.create();

		downloadProgress
				.distinct()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Integer>() {
					@Override
					public void onCompleted() {
						Log.i(TAG, "Download progress. onCompleted");
					}

					@Override
					public void onError(Throwable e) {
						Log.i(TAG, "Download progress. onError : " + e.getMessage());
					}

					@Override
					public void onNext(Integer progress) {
						Log.i(TAG, "Download progress. onNext : " + progress);
						arcProgress.setProgress(progress);
					}
				});

		String destination = "/mnt/sdcard/softboy.avi";

		obserbableDownload("http://archive.blender.org/fileadmin/movies/softboy.avi", destination)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(success -> {
					resetDownloadButton();
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
					File file = new File(destination);
					intent.setDataAndType(Uri.fromFile(file), "video/avi");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}, error -> {
					Toast.makeText(getActivity(), "Something went south", Toast.LENGTH_SHORT).show();
					resetDownloadButton();
				});
	}

	private void resetDownloadButton() {
		downloadBtn.setText("Download");
		downloadBtn.setClickable(true);
		arcProgress.setProgress(0);
	}

	private Observable<Boolean> obserbableDownload(String source, String destination) {
		return Observable.create(subscriber -> {
			try {
				boolean result = downloadFile(source, destination);
				if (result) {
					subscriber.onNext(true);
					subscriber.onCompleted();
				} else {
					subscriber.onError(new Throwable("Download failed."));
				}
			} catch (Exception e) {
				subscriber.onError(e);
			}
		});
	}

	private boolean downloadFile(String source, String destination) {
		boolean result = false;
		InputStream input = null;
		OutputStream output = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(source);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return false;
			}

			int fileLength = connection.getContentLength();

			input = connection.getInputStream();
			output = new FileOutputStream(destination);

			byte data[] = new byte[4096];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				total += count;

				if (fileLength > 0) {
					int percentage = (int) (total * 100 / fileLength);
					downloadProgress.onNext(percentage);
				}
				output.write(data, 0, count);
			}
			downloadProgress.onCompleted();
			result = true;
		} catch (Exception e) {
			downloadProgress.onError(e);
		} finally {
			try {
				if (output != null) {
					output.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				downloadProgress.onError(e);
			}

			if (connection != null) {
				connection.disconnect();
				downloadProgress.onCompleted();
			}
		}
		return result;
	}
}
