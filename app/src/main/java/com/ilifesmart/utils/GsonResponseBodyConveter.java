package com.ilifesmart.utils;

import com.google.gson.TypeAdapter;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class GsonResponseBodyConveter<T> implements Converter<ResponseBody, Object> {

	private final TypeAdapter<T> mAdapter;


	public GsonResponseBodyConveter(TypeAdapter<T> adapter) {
		this.mAdapter = adapter;
	}

	@Override
	public Object convert(ResponseBody value) throws IOException {
		try {
//			Response response = mAdapter.fromJson(value.charStream());
//			if (response.code == 200) {
//				return response.getResult();
//			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			value.close();
		}

		return null;
	}
}
