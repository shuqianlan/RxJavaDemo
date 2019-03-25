package com.ilifesmart.weather;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Path;

public interface GetWeather_Interface {
	@GET("{loc}/realtime.json")
	Call<ResponseBody> getWeatherInfo(@Path("loc") String loc);
	// 最终url: https://api.caiyunapp.com/v2/5Jn=rqANZl-i590W/120.2,30.3/realtime.json

	@GET("{token}/{loc}/realtime.json")
	Call<ResponseBody> getWeatherInfo(@Path("token") String token, @Path("loc") String loc);
	// 最终url: https://api.caiyunapp.com/v2/5Jn=rqANZl-i590W/120.2,30.3/realtime.json

	@HTTP(method = "GET", path = "{token}/{loc}/realtime.json", hasBody = false)
	Call<ResponseBody> getWeatherInfoV2(@Path("token") String token, @Path("loc") String loc);
	// 最终url: https://api.caiyunapp.com/v2/5Jn=rqANZl-i590W/120.2,30.3/realtime.json
}
