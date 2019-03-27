package com.ilifesmart.utils;

import android.provider.SyncStateContract;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ilifesmart.App;

import java.io.File;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtils {
	public static final String TAG = "RetrofitUtils";


	public static OkHttpClient.Builder getOkHttpClientBuilder() {
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
			@Override
			public void log(String message) {
				try {
					Log.d(TAG, "log: " + URLDecoder.decode(message, "utf-8"));
				} catch (Exception ex) {
					ex.printStackTrace();
					Log.e(TAG, "log: " + ex.getMessage());
				}
			}
		});

		loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

		File cacheFile = new File(App.getContext().getExternalCacheDir(), "cache");
		Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); // 100M

		return new OkHttpClient.Builder()
						.readTimeout(10, TimeUnit.SECONDS)
						.connectTimeout(10, TimeUnit.SECONDS)
						.addInterceptor(loggingInterceptor)
						.cache(cache);
	}

	public static Retrofit.Builder getRetrofitBuilder(String baseUrl) {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
		OkHttpClient okHttpClient = getOkHttpClientBuilder().build();
		return new Retrofit.Builder()
						.baseUrl(baseUrl)
						.client(okHttpClient)
						.addConverterFactory(GsonConverterFactory.create(gson))
//						.addCallAdapterFactory(RxJava2Call)
						;
	}
}
