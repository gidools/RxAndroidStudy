package maxst.com.rxandroidstudy.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import maxst.com.rxandroidstudy.R;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AsyncHttpFragment extends Fragment {

	private static final String TAG = AsyncHttpFragment.class.getSimpleName();
	private CompositeSubscription compositeSubscription;

	@Bind(R.id.list_exhibition)
	ListView exhibitionListView;

	@Bind(R.id.btn_start_operation)
	Button startBtn;

	private ExhibitionAdapter exhibitionAdapter;

	public AsyncHttpFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View layout = inflater.inflate(R.layout.fragment_async_http, container, false);
		ButterKnife.bind(this, layout);

		exhibitionAdapter = new ExhibitionAdapter(getContext(), R.layout.item_exhibition, new ArrayList<>());
		exhibitionListView.setAdapter(exhibitionAdapter);
		compositeSubscription = new CompositeSubscription();
		return layout;
	}

	/**
	 * Json parsing is recommended another thread from UI thread
	 */
	@OnClick(R.id.btn_start_operation)
	public void startOperation() {
		exhibitionAdapter.clear();

		compositeSubscription.add(
				requestExhibitionObservable("http://api.arcube.co.kr/api/exhibitions?ordertype=popular")
//				requestExhibitionObservable("http://api.arcube.co.k/api/exhibitions?ordertype=popular") // Make error intentionally
						.flatMap(response -> {
							Type collectionType = new TypeToken<List<Exhibition>>() { }.getType();
							Gson gson = new GsonBuilder().create();
							List<Exhibition> exhibitions = gson.fromJson(response, collectionType);
							Log.d(TAG, "Current thread is main thread : " + (Looper.myLooper() == Looper.getMainLooper()));
							return Observable.from(exhibitions);
						})
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(exhibition -> exhibitionAdapter.add(exhibition)
								, error -> Toast.makeText(getContext(), "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show()
								, () -> Toast.makeText(getContext(), "onCompleted", Toast.LENGTH_SHORT).show()
						)
		);
	}

	private Observable<String> requestExhibitionObservable(String url) {
		return Observable.create(new Observable.OnSubscribe<String>() {

			@Override
			public void call(Subscriber<? super String> observer) {
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
					connection.setRequestMethod("GET");
					InputStream responseStream = connection.getInputStream();
					String response = drainStream(responseStream);
					responseStream.close();
					connection.disconnect();
					observer.onNext(response);
				} catch (IOException e) {
					observer.onError(e);
				} finally {
					observer.onCompleted();
				}
			}
		});
	}

	private static String drainStream(InputStream in) {
		Scanner s = new Scanner(in).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		compositeSubscription.unsubscribe();
		ButterKnife.unbind(this);
	}

private static class Exhibition {
	public int id;
	public String name;
	public String price;
}

private static class ExhibitionAdapter extends ArrayAdapter<Exhibition> {

	public ExhibitionAdapter(Context context, int resource, ArrayList<Exhibition> exhibitions) {
		super(context, resource, exhibitions);
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(getContext(), R.layout.item_exhibition, null);
		}

		TextView id = (TextView) convertView.findViewById(R.id.item_id);
		TextView name = (TextView) convertView.findViewById(R.id.item_name);
		TextView price = (TextView) convertView.findViewById(R.id.item_price);

		Exhibition exhibition = getItem(position);
		id.setText(String.valueOf(exhibition.id));
		name.setText(exhibition.name);
		price.setText(exhibition.price);
		return convertView;
	}
}
}
