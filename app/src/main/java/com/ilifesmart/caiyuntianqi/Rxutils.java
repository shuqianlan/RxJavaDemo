package com.ilifesmart.caiyuntianqi;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class Rxutils {

	public static <T> SingleSource<T> toSimpleSingle(Single<T> upstream) {
		return upstream.observeOn(AndroidSchedulers.mainThread())
						.subscribeOn(Schedulers.io());
	}
}
