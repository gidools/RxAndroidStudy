package maxst.com.rxandroidstudy.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.R.attr.bitmap;

public class DisplayBitmapFragment extends Fragment {

	private static final String TAG = DisplayBitmapFragment.class.getSimpleName();
	private CompositeSubscription compositeSubscription;

	@Bind(R.id.list_exhibition)
	ListView exhibitionListView;

	@Bind(R.id.btn_start_operation)
	Button startBtn;

	private ExhibitionAdapter exhibitionAdapter;

	public DisplayBitmapFragment() {
		// Required empty public constructor
	}

	public static DisplayBitmapFragment newInstance(String param1, String param2) {
		DisplayBitmapFragment fragment = new DisplayBitmapFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View layout = inflater.inflate(R.layout.fragment_display_bitmap, container, false);
		ButterKnife.bind(this, layout);

		exhibitionAdapter = new ExhibitionAdapter(getContext(), R.layout.item_exhibition, new ArrayList<>());
		exhibitionListView.setAdapter(exhibitionAdapter);
		compositeSubscription = new CompositeSubscription();
		return layout;
	}

	@OnClick(R.id.btn_start_operation)
	public void startOperation() {
		exhibitionAdapter.clear();

		compositeSubscription.add(
				requestExhibitionObservable("http://api.arcube.co.kr/api/exhibitions?ordertype=popular")
//				requestExhibitionObservable("http://api.arcube.co.k/api/exhibitions?ordertype=popular") // Make error intentionally
						.flatMap(response -> {
							Type collectionType = new TypeToken<List<Exhibition>>() {}.getType();
							Gson gson = new GsonBuilder().create();
							List<Exhibition> exhibitions = gson.fromJson(response, collectionType);
							Log.d(TAG, "Current thread is main thread : " + (Looper.myLooper() == Looper.getMainLooper()));
							return Observable.from(exhibitions);
						})
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(exhibitionAdapter::add // onNext
								, error -> Toast.makeText(getContext(), "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show() // onError
								, () -> Toast.makeText(getContext(), "onCompleted", Toast.LENGTH_SHORT).show() // onComplete
						)
		);
	}

	private Observable<String> requestExhibitionObservable(String url) {
		return Observable.create(new Observable.OnSubscribe<String>() {

			@Override
			public void call(Subscriber<? super String> observer) {
				try {
					HttpURLConnection connection =
							(HttpURLConnection) new URL(url).openConnection();

					connection.setRequestMethod("GET");
					connection.setUseCaches(false);
					connection.setDoInput(true);
					connection.setConnectTimeout(10 * 1000);
					connection.setReadTimeout(10 * 1000);
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

		@SerializedName("mainimgurl")
		public String mainImgUrl;
	}

	private static class ExhibitionAdapter extends ArrayAdapter<Exhibition> {

		public ExhibitionAdapter(Context context, int resource, ArrayList<Exhibition> exhibitions) {
			super(context, resource, exhibitions);
		}

		@NonNull
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getContext(), R.layout.item_display_bitmap, null);
			}

			TextView id = (TextView) convertView.findViewById(R.id.item_id);
			TextView name = (TextView) convertView.findViewById(R.id.item_name);
			TextView price = (TextView) convertView.findViewById(R.id.item_price);
			ImageView image = (ImageView) convertView.findViewById(R.id.item_image);

			Exhibition exhibition = getItem(position);
			id.setText(String.valueOf(exhibition.id));
			name.setText(exhibition.name);
			price.setText(exhibition.price);

			loadImage(image, exhibition.mainImgUrl);

			return convertView;
		}

		private void loadImage(ImageView imageView, String url) {
			unsubscribeAsync(imageView);

			Subscription subscription = getBitmapObservableFromRemote(url)
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(imageView::setImageBitmap
							, onError -> Log.d(TAG, "Error : " + onError.getMessage())
							, () -> Log.d(TAG, "Completed"));

			imageView.setTag(subscription);
		}

		private void unsubscribeAsync(ImageView imageView) {
			final Object subscription = imageView.getTag();
			if (subscription instanceof Subscription) {
				((Subscription) subscription).unsubscribe();
			}
		}

		private Observable<Bitmap> getBitmapObservableFromRemote(String url) {
			return Observable.create(new Observable.OnSubscribe<Bitmap>() {

				@Override
				public void call(Subscriber<? super Bitmap> observer) {
					try {
						HttpURLConnection connection =
								(HttpURLConnection) new URL(url).openConnection();

						connection.setRequestMethod("GET");
						connection.setUseCaches(false);
						connection.setDoInput(true);
						connection.setConnectTimeout(10 * 1000);
						connection.setReadTimeout(10 * 1000);
						InputStream responseStream = connection.getInputStream();
						Bitmap myBitmap = BitmapFactory.decodeStream(responseStream);
						observer.onNext(myBitmap);
					} catch (IOException e) {
						observer.onError(e);
					} finally {
						observer.onCompleted();
					}
				}
			});
		}
	}
}
