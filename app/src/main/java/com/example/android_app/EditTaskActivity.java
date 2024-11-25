package com.example.android_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.android_app.api.UpdateTaskAPI;
import com.example.android_app.models.Task;
import com.example.android_app.utils.RetrofitInstance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditTaskActivity extends AppCompatActivity {

    private Task task; // Store the task object
    private EditText title, description, deadline;
    private Spinner priority, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_task);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        task = (Task) getIntent().getSerializableExtra("task");


        TextView editTaskTxt = findViewById(R.id.editTaskTextView);
        editTaskTxt.setText("Edit your task");

        TextView editTilteTxt = findViewById(R.id.editTaskTitleTextView);
        editTilteTxt.setText("Title");

        title = findViewById(R.id.editTitle);
        title.setText(task.getTitle());

        TextView editDescTxt = findViewById(R.id.editDescriptionTextView);
        editDescTxt.setText("Description");

        description = findViewById(R.id.editDescription);
        description.setText(task.getDescription());

        TextView editPriorityTxt = findViewById(R.id.editPriorityTextView);
        editPriorityTxt.setText("Priority");

        // Priority Spinner Setup
        priority = findViewById(R.id.editPrioritySpinner);
        String[] priorityOptions = {"Low", "Medium", "High"};
        setupSpinner(priority, priorityOptions, task.getPriority());


        TextView editStatusTxt = findViewById(R.id.editStatusTextView);
        editStatusTxt.setText("Status");

        // Status Spinner Setup
        status = findViewById(R.id.editStatusSpinner);
        String[] statusOptions = {"Pending", "In Progress", "Completed"};
        setupSpinner(status, statusOptions, task.getStatus());

        // Deadline Setup

        TextView editDeadlineTxt = findViewById(R.id.editDeadlineTextView);
        editDeadlineTxt.setText("Deadline");

        deadline = findViewById(R.id.editDeadline);
        deadline.setText(task.getDeadline());
        deadline.setOnClickListener(v -> openDatePicker());

        Button editAddTaskBtn = findViewById(R.id.editAddTaskButton);
        editAddTaskBtn.setText("Save");

        Button editCancelBtn = findViewById(R.id.editCancelButton);
        editCancelBtn.setText("Cancel");




    }
    public void onSaveBtnClick(View view){
        String updatedTitle = title.getText().toString();
        String updatedDescription = description.getText().toString();
        String updatedPriority = priority.getSelectedItem().toString();
        String updatedStatus = status.getSelectedItem().toString();
        String updatedDeadline = deadline.getText().toString().trim();

        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDeadline = "";
        try {
            Date date = inputDateFormat.parse(updatedDeadline);
            if (date != null) {
                formattedDeadline = outputDateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid Date Format", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("EditTaskActivity", "Updated Title: " + updatedTitle);
        Log.d("EditTaskActivity", "Updated Description: " + updatedDescription);
        Log.d("EditTaskActivity", "Updated Priority: " + updatedPriority);
        Log.d("EditTaskActivity", "Updated Status: " + updatedStatus);
        Log.d("EditTaskActivity", "Updated Deadline: " + updatedDeadline);
        if (task != null) {
            task.setTitle(updatedTitle);
            task.setDescription(updatedDescription);
            task.setPriority(updatedPriority);
            task.setStatus(updatedStatus);
            task.setDeadline(formattedDeadline);
        }

        int taskId = task.getId();

        // Create Retrofit instance
        UpdateTaskAPI updateTaskAPI = RetrofitInstance.getRetrofitInstance().create(UpdateTaskAPI.class);

        // Make the API call
        Call<Task> call = updateTaskAPI.updateTask(taskId, task);

        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                    Log.d("EditTaskActivity", "Task updated successfully: " + response.body());
                    Toast.makeText(EditTaskActivity.this, "Task Updated Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditTaskActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(EditTaskActivity.this, "Failed to Update task", Toast.LENGTH_SHORT).show();
                    Log.e("EditTaskActivity", "Error updating task: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                // Handle failure
                Log.e("EditTaskActivity", "Failed to update task: " + t.getMessage());
            }
        });

    }

    public void onEditCancelBtnClick(View view){
        Intent intent = new Intent(EditTaskActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupSpinner(Spinner spinner, String[] options, String selectedValue) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Find and set the index of the selected value
        int selectedIndex = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i].equalsIgnoreCase(selectedValue)) {
                selectedIndex = i;
                break;
            }
        }
        spinner.setSelection(selectedIndex);
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(EditTaskActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    deadline.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

}