package com.example.android_app.api;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface DeleteTaskAPI {
    @DELETE("api/tasks/delete/{task_id}/")
    Call<Void> deleteTask(@Path("task_id") int taskId);
}
