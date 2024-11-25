package com.example.android_app.api;

import com.example.android_app.models.Task;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UpdateTaskAPI {

    @PUT("api/tasks/edit/{task_id}/")  // The endpoint for editing a task, with task_id as a path parameter
    Call<Task> updateTask(@Path("task_id") int taskId, @Body Task task);
}

