package com.ilifesmart.rxjavademo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.ilifesmart.Utils;
import com.ilifesmart.weather.GetWeather_Interface;
import com.ilifesmart.weather.Weather;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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
				Utils.runOnRetrofit();
			});
		} else if (v.getId() == R.id.Retrofit_concat) {
			// concat：串行发送; merge: 并行发送
			Observable.concat(Observable.just(1, 2), Observable.just(3, 4), Observable.just(5, 6), Observable.just(7))
							.subscribe(new Consumer<Integer>() {
								@Override
								public void accept(Integer integer) throws Exception {
									Log.d(TAG, "accept: int " + integer);
								}
							});
		} else if (v.getId() == R.id.Retrofit_zip) {
			Observable.zip(Observable.interval(1, 5, TimeUnit.SECONDS).map(new Function<Long, String>() {
				@Override
				public String apply(Long aLong) throws Exception {
					return "A" + aLong;
				}
			}), Observable.interval(1, 5, TimeUnit.SECONDS).map(new Function<Long, String>() {
				@Override
				public String apply(Long aLong) throws Exception {
					return "B" + aLong;
				}
			}), new BiFunction<String, String, String>() {
				@Override
				public String apply(String s, String s2) {
					return s + s2;
				}
			}).subscribe(new Consumer<String>() {
				@Override
				public void accept(String s) throws Exception {
					Log.d(TAG, "accept: zip_Result " + s);
				}
			});
		} else if (v.getId() == R.id.Retrofit_reduce) {
			Observable.just(1,2,3,4,5,6)
							.reduce(new BiFunction<Integer, Integer, Integer>() {
								@Override
								public Integer apply(Integer integer, Integer integer2) throws Exception {
									return integer + integer2;
								}
							}).subscribe(new Consumer<Integer>() {
				@Override
				public void accept(Integer integer) throws Exception {
					Log.d(TAG, "accept: Result " + integer);
				}
			});
		} else if (v.getId() == R.id.Retrofit_collect) {
			Observable.just(1,2,3,4,5,6)
							.collect(new Callable<ArrayList<Integer>>() {
								@Override
								public ArrayList<Integer> call() {
									return new ArrayList<>();
								}
							}, new BiConsumer<ArrayList<Integer>, Integer>() {
								@Override
								public void accept(ArrayList<Integer> integers, Integer integer) throws Exception {
									integers.add(integer);
								}
							}).subscribe(new Consumer<ArrayList<Integer>>() {
				@Override
				public void accept(ArrayList<Integer> integers) throws Exception {
					Log.d(TAG, "accept: collect_result " + integers); // [1,2,3,4,5,6]
				}
			});
		} else if (v.getId() == R.id.Retrofit_retry_when) {
			Observable.create(new ObservableOnSubscribe<String>() {
				@Override
				public void subscribe(ObservableEmitter<String> emitter) throws Exception {
					emitter.onNext("1");
					emitter.onNext("2");
					emitter.onNext("3");
					emitter.onNext("4");
					emitter.onError(new Exception("404"));
				}
			}).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
				@Override
				public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
					return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
						@Override
						public ObservableSource<?> apply(Throwable throwable) throws Exception {
							Log.d(TAG, "apply: Error " + throwable.getMessage());
							if (throwable.getMessage().equalsIgnoreCase("404")) {
								return Observable.empty();
							} else {
								return Observable.error(throwable);
							}
						}
					});
				}
			}).subscribe(new Observer<String>() {
				@Override
				public void onSubscribe(Disposable d) {
					
				}

				@Override
				public void onNext(String integer) {
					Log.d(TAG, "onNext: retry_when_next " + integer);
				}

				@Override
				public void onError(Throwable e) {
					Log.d(TAG, "onError: Error " + e.getMessage());
				}

				@Override
				public void onComplete() {
					Log.d(TAG, "onComplete: complete ");
				}
			});
		} else if (v.getId() == R.id.Retrofit_retry_until) {
			AtomicInteger atomicInteger = new AtomicInteger(1);
			Observable.create(new ObservableOnSubscribe<String>() {
				@Override
				public void subscribe(ObservableEmitter<String> emitter) throws Exception {
					emitter.onNext("1");
					emitter.onNext("2");
					emitter.onNext("3");
					emitter.onError(new Exception("404"));
					emitter.onNext("4");
				}})
				.retryUntil(new BooleanSupplier() {
					@Override
					public boolean getAsBoolean() throws Exception {
						atomicInteger.getAndIncrement();

						return atomicInteger.get() > 2; // true:不再重试 false:继续重试
					}})
				.subscribe(new Observer<String>() {
					@Override
					public void onSubscribe(Disposable d) {
						Log.d(TAG, "onSubscribe: " + d.isDisposed());
					}

					@Override
					public void onNext(String s) {
						Log.d(TAG, "onNext: s " + s);
					}

					@Override
					public void onError(Throwable e) {
						Log.d(TAG, "onError: Error " + e.getMessage());
					}

					@Override
					public void onComplete() {
						Log.d(TAG, "onComplete: complete");
					}
				});
		} else if (v.getId() == R.id.Retrofit_add_then) {
			Observable.just("1", "2", "3", "4")
			.ignoreElements()
			.andThen(Observable.just("Complete"))
//			.doFinally(new Action() {
//				@Override
//				public void run() throws Exception {
//					Log.d(TAG, "run: -------- finally");
//				}
//			})
			.subscribe(new Observer<String>() {
				@Override
				public void onSubscribe(Disposable d) {
					Log.d(TAG, "onSubscribe: ");
				}

				@Override
				public void onNext(String s) {
					Log.d(TAG, "onNext: s " + s);
				}

				@Override
				public void onError(Throwable e) {
					Log.d(TAG, "onError: error " + e.getMessage());
				}

				@Override
				public void onComplete() {
					Log.d(TAG, "onComplete: ");
				}
			});

		}
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
//
//		Observable.combineLatest(
//						Observable.just(1L, 2L, 3L), // 第1个发送数据事件的Observable
//						Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS), // 第2个发送数据事件的Observable：从0开始发送、共发送3个数据、第1次事件延迟发送时间 = 1s、间隔时间 = 1s
//						new BiFunction<Long, Long, Long>() {
//							@Override
//							public Long apply(Long o1, Long o2) throws Exception {
//								// o1 = 第1个Observable发送的最新（最后）1个数据
//								// o2 = 第2个Observable发送的每1个数据
//								Log.e(TAG, "合并的数据是： "+ o1 + " "+ o2);
//								return o1 + o2;
//								// 合并的逻辑 = 相加
//								// 即第1个Observable发送的最后1个数据 与 第2个Observable发送的每1个数据进行相加
//							}
//						}).subscribe(new Consumer<Long>() {
//			@Override
//			public void accept(Long s) throws Exception {
//				Log.e(TAG, "合并的结果是： "+s);
//			}
//		});
	}
}
