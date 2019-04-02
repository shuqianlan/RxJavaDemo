package com.ilifesmart.rxjavademo;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class BaseActivity extends AppCompatActivity {

	protected void postEvent(Object object) {
		RxBus.getRxBus().post(object);
	}

	protected <M> void addSubscription(Class<M> eventType, Consumer<M> action) {
		// doSubscribe中对目标Class已做过滤. 并且已经背压处理.
		Disposable disposable = RxBus.getRxBus().doSubscribe(eventType, action, new Consumer<Throwable>() {
			@Override
			public void accept(Throwable throwable) throws Exception {
				Log.d("RxBus", "accept: Error " + throwable.getMessage());
			}
		});
		RxBus.getRxBus().addSubscription(this, disposable);
	}

	protected <M> void addSubscription(Class<M > eventType, Consumer<M> action, Consumer<Throwable> error) {
		Disposable disposable = RxBus.getRxBus().doSubscribe(eventType, action, error);
		RxBus.getRxBus().addSubscription(this, disposable);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RxBus.getRxBus().unSubscribe(this);
	}
}
