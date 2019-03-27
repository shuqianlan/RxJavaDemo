package com.ilifesmart.caiyuntianqi;

import com.ilifesmart.weather.Weather;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CaiYunTianQi_Interface {

	/* 实况天气接口 */
	@GET("{loc}/realtime.json")
	Single<Weather> getRealTimeWeather(@Path("loc") String loc);

	/* 分钟级降雨预报接口 */
	@GET("{loc}/minutely.json")
	Single<Object> getMinutelyWeather(@Path("loc") String loc);

	/* 小时级降雨预报接口 */
	@GET("{loc}/hourly.json")
	Single<Object> getHourlyWeather(@Path("loc") String loc);

	/* 天级降雨预报接口 */
	@GET("{loc}/daily.json")
	Single<Object> getDailyWeather(@Path("loc") String loc);

}
