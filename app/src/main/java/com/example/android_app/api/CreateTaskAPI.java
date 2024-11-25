package com.example.android_app.api;


import com.example.android_app.models.Task;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface CreateTaskAPI {
    @Headers("Content-Type: application/json")
    @POST("api/tasks/create")
    Call<Void> createTask(@Body Task createTask);

}