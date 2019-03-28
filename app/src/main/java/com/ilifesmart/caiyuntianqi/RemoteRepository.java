package com.ilifesmart.caiyuntianqi;

import com.ilifesmart.weather.Weather;

import io.reactivex.Single;
import retrofit2.Retrofit;

public class RemoteRepository {

	private static RemoteRepository sInstance;

	private Retrofit mRetrofit;
	private CaiYunTianQi_Interface mWeatherApi;

	/*
	* 基础网络调用.
	* 返回类型为Single，仅onSuccess或onError，匹配RxJavaCallAdapterFactory.class
	* GsonConverter Response格式转换.
	*
	* */

	public RemoteRepository() {
		mRetrofit = RetrofitHelper.getInstance().getRetrofit();
		mWeatherApi = mRetrofit.create(CaiYunTianQi_Interface.class);
	}

	public static RemoteRepository getInstance() {
		if (sInstance == null) {
			synchronized (RemoteRepository.class) {
				if (sInstance == null) {
					sInstance = new RemoteRepository();
				}
			}
		}

		return sInstance;
	}

	// 天气接口.
	public Single<Weather> getRealTimeWeather(String loc) {
		return mWeatherApi.getRealTimeWeather(loc);
	}

}
