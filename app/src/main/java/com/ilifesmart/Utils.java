package com.ilifesmart;

import android.util.Log;

import com.google.gson.Gson;
import com.ilifesmart.weather.GetWeather_Interface;
import com.ilifesmart.weather.Weather;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utils {
	public static final String TAG = "Weather";

	public static String runOnRetrofit() {
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
/* 异步 */
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

	/*
	* 小时级天气预报
	* @
	* */
	public static String runOnRetrofitQuery() {
		Retrofit retrofit = new Retrofit.Builder()
						.baseUrl("https://api.caiyunapp.com/v2/")
						.build();

		GetWeather_Interface weather_interface = retrofit.create(GetWeather_Interface.class);
		Call<ResponseBody> call = weather_interface.getWeatherHourlyInfo("5Jn=rqANZl-i590W", "120.2,30.3", "en_US", 2);

		try {
			Response<ResponseBody> response = call.execute();
			return response.body().string();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
