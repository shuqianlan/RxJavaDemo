package com.ilifesmart.rxjavademo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.ilifesmart.Utils;
import com.ilifesmart.weather.GetWeather_Interface;
import com.ilifesmart.weather.Weather;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ilifesmart.Utils.runOnRetrofit;

public class DemoActivity extends BaseActivity {

	public static final String TAG = "DemoActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);
	}

	@Override
	protected void onResume() {
		super.onResume();

		postEvent(new RxMessage("Hello,RxJava"));
	}

	public void OnClick(View v) {
		if (v.getId() == R.id.Retrofit_Path) {
			Observable.create(new ObservableOnSubscribe<String>() {
				@Override
				public void subscribe(ObservableEmitter<String> emitter) {
					String result = runOnRetrofit();
					emitter.onNext(result);
					emitter.onComplete();
				}
			})
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Consumer<String>() {
				@Override
				public void accept(String s) throws Exception {
					postEvent(new RxMessage(s)); // 自定义EventBus
				}
			});
		} else if (v.getId() == R.id.Retrofit_Query) {
			Observable.create(new ObservableOnSubscribe<String>() {
				@Override
				public void subscribe(ObservableEmitter<String> emitter) {
					String result = Utils.runOnRetrofitQuery();
					emitter.onNext(result);
				}
			})
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Consumer<String>() {
				@Override
				public void accept(String s) throws Exception {
					Log.d(TAG, "accept: Result " + s);
				}
			});

		}
	}

}
