package com.example.android_app.api;

import com.example.android_app.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterAPI {

    // Define the POST request method for user registration
    @POST("api/register/")  // Django URL endpoint for registration (adjust it accordingly)
    Call<Void> registerUser(@Body RegisterRequest registerRequest);


}
