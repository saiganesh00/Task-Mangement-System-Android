package com.example.android_app.utils;

import android.util.Log;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitInstance {

    private static Retrofit retrofit;
    private static OkHttpClient client;
    private static final String BASE_URL = "http://172.16.20.67:8000/";
    private static String authToken = null;
    private static String authPrefix = "Bearer"; // Default to "Bearer"

    // Set the authorization token
    public static void setAuthToken(String token) {
        authToken = token;
        retrofit = null; // Invalidate the retrofit instance so it will be recreated with the new token
    }

    // Get the current authorization token
    public static String getAuthToken() {
        return authToken;
    }

    // Set the authorization prefix (e.g., "Bearer")
    public static void setAuthPrefix(String prefix) {
        authPrefix = prefix;
        retrofit = null; // Invalidate the retrofit instance to apply new prefix
    }

    // Create and return a new OkHttpClient instance with an authorization header interceptor
    private static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request originalRequest = chain.request();
                            Request.Builder requestBuilder = originalRequest.newBuilder();

                            // Add the Authorization header if the token is available
                            if (authToken != null && !authToken.isEmpty()) {
                                requestBuilder.addHeader("Authorization", authPrefix + " " + authToken);
                            }

                            Log.d("Retrofit", "Request URL: " + originalRequest.url());
                            Log.d("Retrofit", "Request Method: " + originalRequest.method());

                            return chain.proceed(requestBuilder.build());
                        }
                    })
                    .build();
        }
        return client;
    }

    // Get or create the Retrofit instance with the necessary configurations
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
