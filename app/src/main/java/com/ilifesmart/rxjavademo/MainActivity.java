package com.ilifesmart.rxjavademo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.ilifesmart.weather.GetWeather_Interface;
import com.ilifesmart.weather.Weather;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.operators.flowable.FlowableInternalHelper;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		addSubscription(RxMessage.class, new Consumer<RxMessage>() {
			@Override
			public void accept(RxMessage rxMessage) throws Exception {
				Log.d("RxMessage", "accept: message " + rxMessage.toString());
			}
		}, Functions.ON_ERROR_MISSING);

//		observableMerge();
	}

	public void onClick(View v) {
		if (v.getId() == R.id.click) {
			startActivity(new Intent(this, DemoActivity.class));
		} else if (v.getId() == R.id.Retrofit) {
			// Retrofit 天气数据测试. 线程池调用..
			AsyncTask.THREAD_POOL_EXECUTOR.execute(()->{
				runOnRetrofit();
			});

			//
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
					Log.d(TAG, "accept: result " + s);
				}
			});
		}
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

	public static final String TAG = "RxJava";
	private void observableMerge() {
//		Observable ob_1 = Observable.just("A");
//		Observable ob_2 = Observable.just("B");
//
//		final StringBuilder builder = new StringBuilder("Result: ");
//		Observable.merge(ob_1, ob_2)
//			.subscribe(new Observer() {
//			@Override
//			public void onSubscribe(Disposable d) {
//				Log.d(TAG, "onSubscribe: ");
//			}
//
//			@Override
//			public void onNext(Object o) {
//				Log.d(TAG, "onNext: " + o);
//				builder.append(o);
//			}
//
//			@Override
//			public void onError(Throwable e) {
//				Log.d(TAG, "onError: message " + e.getMessage());
//			}
//
//			@Override
//			public void onComplete() {
//				Log.d(TAG, "onComplete: " + builder.toString());
//			}
//		});

		Observable.combineLatest(
						Observable.just(1L, 2L, 3L), // 第1个发送数据事件的Observable
						Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS), // 第2个发送数据事件的Observable：从0开始发送、共发送3个数据、第1次事件延迟发送时间 = 1s、间隔时间 = 1s
						new BiFunction<Long, Long, Long>() {
							@Override
							public Long apply(Long o1, Long o2) throws Exception {
								// o1 = 第1个Observable发送的最新（最后）1个数据
								// o2 = 第2个Observable发送的每1个数据
								Log.e(TAG, "合并的数据是： "+ o1 + " "+ o2);
								return o1 + o2;
								// 合并的逻辑 = 相加
								// 即第1个Observable发送的最后1个数据 与 第2个Observable发送的每1个数据进行相加
							}
						}).subscribe(new Consumer<Long>() {
			@Override
			public void accept(Long s) throws Exception {
				Log.e(TAG, "合并的结果是： "+s);
			}
		});
	}
}
