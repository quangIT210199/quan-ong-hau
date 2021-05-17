package com.codelovers.quanonghau.utils;



import com.codelovers.quanonghau.api.Api;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiConnection {
    private static Api retrofitAPI;
    private static ApiConnection _instance;
    private ApiConnection(){
    }

    public Api getRetrofitAPI() {
        return retrofitAPI;
    }

    public static ApiConnection getInstance(){
        if(_instance==null){
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder().client(new OkHttpClient.Builder()
                    .connectTimeout(600000, TimeUnit.MILLISECONDS)
                    .readTimeout(600000, TimeUnit.MILLISECONDS)
                    .build())
                    .baseUrl("base_url")
//                    .baseUrl(ConstantsForConfig.api_base)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            retrofitAPI = retrofit.create(Api.class);
            _instance = new ApiConnection();
            return _instance;
        }
        return _instance;
    }
}
