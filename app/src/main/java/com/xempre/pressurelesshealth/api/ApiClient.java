package com.xempre.pressurelesshealth.api;

import com.xempre.pressurelesshealth.interfaces.MeasurementService;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://health.xempre.com";

    private static Retrofit retrofit;

    public static <T> T createService(Context context, Class<T> serviceClass, Integer option) {
        if (retrofit == null) {

            SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token", "");
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            if (option==1){
                httpClient.addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", "Token " + token)
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                });
            }


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        return retrofit.create(serviceClass);
    }

    public static void destroy(){
        retrofit = null;
    }
}



