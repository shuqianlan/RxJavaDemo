package com.ilifesmart.rxjavademo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
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
								postEvent(new RxMessage(s));
							}
						});

	}

	private String runOnRetrofit() {
		Retrofit retrofit = new Retrofit.Builder()
						.baseUrl("https://api.caiyunapp.com/v2/")
						.addConverterFactory(GsonConverterFactory.create())
						.build();

		GetWeather_Interface weather = retrofit.create(GetWeather_Interface.class);
		Call<ResponseBody> call = weather.getWeatherInfoV2("5Jn=rqANZl-i590W", "120.2,30.3");

		try {
			Response<ResponseBody> response = call.execute();
			String json = response.body().string();
			Log.d(TAG, "onResponse: response " + json);
			Weather weather1 = new Gson().fromJson(json, Weather.class);
			Log.d(TAG, "onResponse: status " + weather1.getApi_status());
			Log.d(TAG, "onResponse: version " + weather1.getApi_version());
			Log.d(TAG, "onResponse: comfort " + weather1.getResult().getComfort());
			Log.d(TAG, "onResponse: aqi " + weather1.getResult().getAqi());

			return json;
		} catch (Exception ex) {
			Log.d(TAG, "runOnRetrofit: Error " + ex.getMessage());
		}

		return null;
//		call.enqueue(new Callback<ResponseBody>() {
//			@Override
//			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//				try {
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//			}
//
//			@Override
//			public void onFailure(Call<ResponseBody> call, Throwable t) {
//				Log.d(TAG, "onFailure: Error " + t.getMessage());
//			}
//		});
	}

}
