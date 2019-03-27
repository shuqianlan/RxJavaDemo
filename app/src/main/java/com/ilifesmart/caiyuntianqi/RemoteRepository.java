package com.ilifesmart.caiyuntianqi;

import com.ilifesmart.weather.Weather;

import io.reactivex.Single;
import retrofit2.Retrofit;

public class RemoteRepository {

	private static RemoteRepository sInstance;

	private Retrofit mRetrofit;
	private CaiYunTianQi_Interface mWeatherApi;

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

	public Single<Weather> getRealTimeWeather(String loc) {
		return mWeatherApi.getRealTimeWeather(loc);
	}
}
