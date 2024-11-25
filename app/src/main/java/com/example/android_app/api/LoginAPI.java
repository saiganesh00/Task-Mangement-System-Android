package com.example.android_app.api;

import com.example.android_app.models.LoginRequest;
import com.example.android_app.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginAPI {

    // Define the POST request method for user login
    @POST("api/login/")  // Django URL endpoint for login (adjust it accordingly)
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
}
