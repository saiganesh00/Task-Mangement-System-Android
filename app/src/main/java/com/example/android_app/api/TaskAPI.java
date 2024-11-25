package com.example.android_app.api;

import com.example.android_app.models.Task;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

import java.util.List;

public interface TaskAPI {
    @GET("api/tasks/") // Adjust the endpoint as per your backend
    Call<List<Task>> getTasks();
}
