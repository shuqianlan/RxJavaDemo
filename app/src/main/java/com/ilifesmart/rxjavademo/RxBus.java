package com.ilifesmart.rxjavademo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {

	private static volatile RxBus rxBus;
	private final Subject<Object> subject = PublishSubject.create().toSerialized(); // 确保onXXX()是线程安全的

	private Map<String, CompositeDisposable> mDisposableMap = new ConcurrentHashMap<>();

	public static RxBus getRxBus() {
		if (rxBus == null) {
			synchronized (RxBus.class) {
				if (rxBus == null) {
					rxBus = new RxBus();
				}
			}
		}

		return rxBus;
	}

	public <T> Flowable<T> getObservable(Class<T> type) {
		// 返回一个带背压的缓存直至消耗的指定类型的可观察者.
		return subject.toFlowable(BackpressureStrategy.BUFFER).ofType(type); // 缓存所有的onNext()直至被消耗.仅保留发射type的类型
	}

	public <T> Flowable<T> getObservable(Class<T> type, Flowable flowable) {
		// 返回一个带背压的缓存直至消耗的指定类型的可观察者.
		return subject.toFlowable(BackpressureStrategy.BUFFER).ofType(type); // 缓存所有的onNext()直至被消耗.仅保留发射type的类型
	}

	public void addSubscription(Object o, Disposable disposable) {
		addSubscription(o.getClass(), disposable);
	}

	public void addSubscription(Class clz, Disposable disposable) {
		String key = clz.getName();

		// 基于类型名的多观察者缓存，用于生命周期内有效.
		if (mDisposableMap.containsKey(key)) {
			mDisposableMap.get(key).add(disposable);
		} else {
			CompositeDisposable disposable1 = new CompositeDisposable(disposable);
			mDisposableMap.put(key, disposable1);
		}
	}

	public void unSubscribe(Object o) {
		String key = o.getClass().getName();
		if (!mDisposableMap.containsKey(key)) {
			return;
		}

		if (mDisposableMap.get(key) != null) {
			mDisposableMap.get(key).dispose();
		}

		mDisposableMap.remove(key);
	}

	public void post(Object o) {
		// 消息通知.
		subject.onNext(o);
	}

	public <T> Disposable doSubscribe(Class<T> type, Consumer<T> next, Consumer<Throwable> error) {
		return getObservable(type)
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(next, error);
	}

}
