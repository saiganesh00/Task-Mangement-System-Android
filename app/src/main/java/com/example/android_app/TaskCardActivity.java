package com.example.android_app;

import android.os.Bundle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_app.adapters.TaskAdapter;
import com.example.android_app.api.DeleteTaskAPI;
import com.example.android_app.models.Task;
import com.example.android_app.utils.RetrofitInstance;
import java.util.ArrayList;
import java.util.List;

public class TaskCardActivity extends AppCompatActivity {

    private List<Task> tasks = new ArrayList<>();  // List to store tasks
    private TaskAdapter taskAdapter;  // The adapter used for the RecyclerView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_card);

        TextView taskTitle = findViewById(R.id.taskCardTitle);
        taskTitle.setText("Title");

        TextView taskDescription = findViewById(R.id.taskCardDescription);
        taskDescription.setText("Description");

        TextView taskPriority = findViewById(R.id.taskCardPriority);
        taskPriority.setText("Priority: High");

        TextView taskStatus = findViewById(R.id.taskCardStatus);
        taskStatus.setText("Status: In Progress");

        Button editBtn = findViewById(R.id.taskCardEditButton);
        editBtn.setText("Edit");

        Button deleteBtn = findViewById(R.id.taskCardDeleteButton);
        deleteBtn.setText("Delete");

        int taskId = getIntent().getIntExtra("TASK_ID", -1);
        int taskPosition = getIntent().getIntExtra("TASK_POSITION", -1);

        if (taskId != -1) {
            Log.d("TaskCardActivity", "Received taskId: " + taskId);
        } else {
            Log.e("TaskCardActivity", "Task ID not received!");
        }

        // Assuming tasks is populated somewhere in your app
        // e.g., tasks.addAll(getTasksFromDatabase());

        deleteBtn.setOnClickListener(v -> {
            if (taskId != -1) {
                deleteTask(taskId, taskPosition); // Call deleteTask with the received taskId
            } else {
                Toast.makeText(this, "Error: Task ID not available.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onEditButtonClick(View view){
        // Handle the edit button click logic

    }

    private void deleteTask(int taskId, int position) {
        // Create an instance of the DeleteTaskAPI
        DeleteTaskAPI deleteTaskAPI = RetrofitInstance.getRetrofitInstance().create(DeleteTaskAPI.class);

        // Call the deleteTask method on the API interface
        Call<Void> call = deleteTaskAPI.deleteTask(taskId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Show a success message
                    Toast.makeText(TaskCardActivity.this, "Task deleted successfully.", Toast.LENGTH_SHORT).show();

                    // Remove the task from the local list of tasks
                    tasks.remove(position);  // `tasks` is the list of tasks in your activity

                    // Notify the adapter that the task was removed from the list
                    taskAdapter.notifyItemRemoved(position);  // `taskAdapter` is your adapter instance

                    // Optionally, finish the activity if needed
                    finish();  // Close TaskCardActivity after deletion
                } else {
                    // Log the error in case of failure
                    Log.e("DeleteTask", "Failed to delete task. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Log the error if the API call fails
                Log.e("DeleteTask", "Error deleting task: " + t.getMessage());
            }
        });
    }

    // Example method to populate tasks (this should be handled appropriately in your app)

}
