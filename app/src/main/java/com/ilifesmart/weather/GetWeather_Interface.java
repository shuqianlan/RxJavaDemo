package com.ilifesmart.weather;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetWeather_Interface {

/*
* ### 请求方法类注解
* - @GET("data/sk/101010100.html") : 可以作为后缀部分
* - @POST
* - @PUT
* - @DELETE
* - @PATCH
* - @HEAD
* - @OPTIONS
* - @HTTP
*
* ### 请求参数类注解
*
* - @Headers 头信息参数
* - @Path 替换url中{xx}的xx内容 GET
* - @Query 接口url追加 http://***?uid=7504552&passwd=****  GET
* - @QueryMap 同Query多参数拼接 GET
* - @Field 类似于Query POST
* - @FieldMap 类似于QueryMap POST
* - @Part 分块请求
* - @PartMap 分块请求
* - @Body 指定对象作为请求体 非表单请求体
```
#Path
@GET("wages/{wageId}/detail")
Call<VideoInfo >getVideoData(@Path("wageId") String wageId);

#Query
//接口:http://www.kuaidi100.com/query?type=yuantong&postid=500379523313"
@GET("query")
Call QueryInfo QueryInfo(@Query("type") String type,@Query("postid") String postid);

#QueryMap
例如接口:
http://www.kuaidi100.com/query?type=yuantong&postid=500379523313"
形成提交参数的部分:
ype=yuantong&postid=500379523313
Call QueryInfo QueryInfo(@QueryMap Map<String, String> map) ;

#Body
@POST("users/new")
Call<User> createUser(@Body User user);
```

### 标记类注解

- @FormUrlEncoded 对表单域中填写的内容进行编码处理,避免乱码. POST
- @MultiPart      Post提交分块请求,如果上传文件必须指定MultiPart
- @Streaming      响应体的数据用流的形式返回

## Note

- Map 用来组合复杂的参数；
					- Query、QueryMap 与 Field、FieldMap 功能一样，生成的数据形式一样；
					- Query、QueryMap 的数据体现在url上,主要用于GET请求.
					- Field、FieldMap 的数据是在请求体上,主要用于Post请求.
*/

	@GET("{loc}/realtime.json")
	Call<ResponseBody> getWeatherInfo(@Path("loc") String loc);
	// 最终url: https://api.caiyunapp.com/v2/5Jn=rqANZl-i590W/120.2,30.3/realtime.json

	@GET("{token}/{loc}/realtime.json")
	Call<ResponseBody> getWeatherInfo(@Path("token") String token, @Path("loc") String loc);
	// 最终url: https://api.caiyunapp.com/v2/5Jn=rqANZl-i590W/120.2,30.3/realtime.json

	@HTTP(method = "GET", path = "{token}/{loc}/realtime.json", hasBody = false)
	Call<ResponseBody> getWeatherInfoV2(@Path("token") String token, @Path("loc") String loc);
	// 最终url: https://api.caiyunapp.com/v2/5Jn=rqANZl-i590W/120.2,30.3/realtime.json

	@GET("{token}/{loc}/hourly")
	Call<ResponseBody> getWeatherHourlyInfo(@Path("token") String token, @Path("loc") String loc, @Query("lang") String lang, @Query("hourlysteps") int hoursstep);
	// 最终url: https://api.caiyunapp.com/v2/5Jn=rqANZl-i590W/120.2,30.3/realtime.json




//
//
//









}
