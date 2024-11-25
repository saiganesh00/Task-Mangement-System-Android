package com.example.android_app;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_app.adapters.TaskAdapter;
import com.example.android_app.api.TaskAPI;
import com.example.android_app.models.Task;
import com.example.android_app.utils.RetrofitInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView taskRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    private CardView profileCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Set welcome text
        TextView welcomeTxt = findViewById(R.id.dashboardWelcomeText);
        welcomeTxt.setText("Welcome Back");

        // EMAIL
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("EMAIL", null);
        if (email != null) {
            Log.d("TAG", "Email is: " + email);
        } else {
            Log.d("TAG", "EMAIL is missing in SharedPreferences.");
        }

        TextView userName = findViewById(R.id.userName);
        userName.setText(email != null ? email.split("@")[0] : "Error");

        // Set logo image
        ImageView logoImg = findViewById(R.id.iconImage);
        logoImg.setImageResource(R.drawable.user_icon);
        profileCard = findViewById(R.id.profileCard);
//        logoImg.setOnClickListener(view -> toggleProfileCard());

        // Set button texts
        Button logoutBtn = findViewById(R.id.logoutButton);
        logoutBtn.setText("Log Out");

        Button createTaskBtn = findViewById(R.id.createTaskButton);
        createTaskBtn.setText("Create Task");

        // Initialize RecyclerView
        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        taskRecyclerView.setAdapter(taskAdapter);

        // Fetch tasks from the server
        fetchTasks();
    }

    private void fetchTasks() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String authToken = sharedPreferences.getString("authToken", null);
        String authPrefix = sharedPreferences.getString("authPrefix", "Token"); // Default to "Token"

        // Ensure auth token is present
        if (authToken == null || authToken.isEmpty()) {
            Toast.makeText(DashboardActivity.this, "Authentication token is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set the token and prefix in RetrofitInstance
        RetrofitInstance.setAuthToken(authToken);
        RetrofitInstance.setAuthPrefix(authPrefix);

        Log.d("FetchTasks", "Authorization Token and Prefix Set in RetrofitInstance");

        // Create Retrofit instance and call API
        TaskAPI taskApi = RetrofitInstance.getRetrofitInstance().create(TaskAPI.class);
        taskApi.getTasks().enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("FetchTasks", "Response Body: " + response.body().toString());
                    for (Task task : response.body()) {
                        if (task.getId() != 0) {  // Assuming 0 is not a valid ID
                            Log.d("FetchTasks", "Task ID: " + task.getId());
                        } else {
                            Log.e("FetchTasks", "Invalid Task ID: " + task.getId());
                        }
                    }

                    taskList.clear();
                    taskList.addAll(response.body());
                    taskAdapter.notifyDataSetChanged();
                } else {
                    if (response.errorBody() != null) {
                        try {
                            Log.e("FetchTasks", "Error Body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(DashboardActivity.this, "Failed to fetch tasks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FetchTasks", "Request Failure: ", t);
            }
        });
    }

    // Logout functionality
    public void onLogoutBtnClick(View view) {
        // Clear the authentication token from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("authToken"); // Remove stored auth token
        editor.apply();
        Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
        // Navigate to LoginActivity
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        startActivity(intent);
        
        profileCard.setVisibility(View.GONE);
        // Finish the current activity so user can't navigate back to it
        finish();
    }
    public void toggleProfileCard(View view) {
        ImageView iconImage = findViewById(R.id.iconImage);

        profileCard = findViewById(R.id.profileCard);
        if (profileCard.getVisibility() == View.GONE) {
            profileCard.setVisibility(View.VISIBLE);
        } else {
            profileCard.setVisibility(View.GONE);
            iconImage.setVisibility(View.VISIBLE);
        }
    }


    // Navigate to CreateTask Activity
    public void onCreateTaskClick(View view) {
        Intent intent = new Intent(DashboardActivity.this, CreateTask.class);
        startActivity(intent);
        finish();
    }
}
