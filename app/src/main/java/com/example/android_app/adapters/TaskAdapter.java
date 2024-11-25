package com.example.android_app.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_app.EditTaskActivity;
import com.example.android_app.R;
import com.example.android_app.api.DeleteTaskAPI;
import com.example.android_app.models.Task;
import com.example.android_app.utils.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    public void updateTaskList(List<Task> newTaskList) {
        this.taskList = newTaskList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each task
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_task_card, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        // Bind task data to the view holder
        Task task = taskList.get(position);
        holder.titleTextView.setText(task.getTitle());
        holder.descriptionTextView.setText(task.getDescription());
        holder.deadlineTextView.setText(task.getDeadline());
        holder.priorityTextView.setText("Priority: " + task.getPriority());
        holder.statusTextView.setText("Status: " + task.getStatus());

        holder.deleteButton.setOnClickListener(v -> {
            // Call the method to delete the task from the server
            deleteTaskFromServer(task.getId(), position, v);
        });

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditTaskActivity.class);

            // Pass the task object to the activity
            intent.putExtra("task", task);

            // Start the activity
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    // Method to delete task from server
    private void deleteTaskFromServer(int taskId, int position, View view) {
        // Use Retrofit to make the API call
        DeleteTaskAPI deleteTaskAPI = RetrofitInstance.getRetrofitInstance().create(DeleteTaskAPI.class);
        Call<Void> call = deleteTaskAPI.deleteTask(taskId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Remove the task from the list and update the UI
                    taskList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(view.getContext(), "Task deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), "Failed to delete task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(view.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ViewHolder class for TaskAdapter
    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView descriptionTextView;
        TextView deadlineTextView;
        TextView priorityTextView;
        TextView statusTextView;
        Button editButton;
        Button deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.taskCardTitle);
            descriptionTextView = itemView.findViewById(R.id.taskCardDescription);
            deadlineTextView = itemView.findViewById(R.id.taskCardDeadline);
            priorityTextView = itemView.findViewById(R.id.taskCardPriority);
            statusTextView = itemView.findViewById(R.id.taskCardStatus);
            editButton = itemView.findViewById(R.id.taskCardEditButton);
            deleteButton = itemView.findViewById(R.id.taskCardDeleteButton);
        }
    }
}
