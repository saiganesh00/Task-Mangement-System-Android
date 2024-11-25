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

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_app.api.CreateTaskAPI;
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

public class CreateTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task); // Ensure this matches your XML layout file name

        // Initialize and set text for the TextViews
        TextView createTaskText = findViewById(R.id.createTaskTextView);
        createTaskText.setText("Create New Task");

        TextView taskTitle = findViewById(R.id.createTaskTitle);
        taskTitle.setText("Title");

        EditText title = findViewById(R.id.taskTitleEditText);

        TextView descTxt = findViewById(R.id.createDescriptionTextView);
        descTxt.setText("Description");

        TextView priorityText = findViewById(R.id.priorityTextView);
        priorityText.setText("Priority");

        Spinner prioritySpinner = findViewById(R.id.prioritySpinner);

        // Priority Spinner Options
        String[] priorityOptions = {"Low", "Medium", "High"};

        // Set up the Priority Spinner with a custom ArrayAdapter
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, priorityOptions);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityAdapter);

        // Set default selection
        prioritySpinner.setSelection(0);

        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPriority = priorityOptions[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        TextView statusText = findViewById(R.id.statusTextView);
        statusText.setText("Status");

        Spinner statusSpinner = findViewById(R.id.statusSpinner);

        // Status Spinner Options
        String[] statusOptions = {"Pending", "In Progress", "Completed"};

        // Set up the Status Spinner with a custom ArrayAdapter
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, statusOptions);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        // Set default selection
        statusSpinner.setSelection(0);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = statusOptions[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        TextView deadlineText = findViewById(R.id.deadlineTextView);
        deadlineText.setText("Deadline");

        EditText deadlineEditText = findViewById(R.id.deadlineEditText);

        deadlineEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTask.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        deadlineEditText.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        Button addTaskBtn = findViewById(R.id.addTaskButton);
        addTaskBtn.setText("Add Task");

        Button cancelBtn = findViewById(R.id.cancelButton);
        cancelBtn.setText("Cancel");

    }

    public void onAddTaskBtnClick(View view) {
        // Get user input values
        EditText titleText = findViewById(R.id.taskTitleEditText);
        String title = titleText.getText().toString();

        EditText descText = findViewById(R.id.taskDescriptionEditText);
        String description = descText.getText().toString();

        Spinner priorityText = findViewById(R.id.prioritySpinner);
        String priority = priorityText.getSelectedItem().toString(); // Get selected priority

        Spinner statusText = findViewById(R.id.statusSpinner);
        String status = statusText.getSelectedItem().toString(); // Get selected status

        EditText deadlineText = findViewById(R.id.deadlineEditText);
        String deadline = deadlineText.getText().toString();

        // Validate the input fields
        if (title.isEmpty() || description.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert the deadline to the correct format (yyyy-MM-dd)
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDeadline = "";
        try {
            Date date = inputDateFormat.parse(deadline);
            if (date != null) {
                formattedDeadline = outputDateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid Date Format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Task object
        Task createTaskRequest = new Task(title, description, formattedDeadline, priority, status);

        // Log the task details
        Log.d("Task", "Title: " + title + "\nDescription: " + description + "\nPriority: " + priority + "\nStatus: " + status + "\nDeadline: " + formattedDeadline);

        // Retrofit request to add the task
        Retrofit retrofit = RetrofitInstance.getRetrofitInstance();
        CreateTaskAPI createTaskAPI = retrofit.create(CreateTaskAPI.class);
        Call<Void> call = createTaskAPI.createTask(createTaskRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateTask.this, "Task Added Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateTask.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("Task Error", "Response Code: " + response.code() + " - " + response.message());
                    Toast.makeText(CreateTask.this, "Failed to Add Task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Task Error", "Error: " + t.getMessage());
                Toast.makeText(CreateTask.this, "Unexpected Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });

        // Clear input fields after task submission
        titleText.setText("");
        descText.setText("");
        deadlineText.setText("");
        priorityText.setSelection(0);  // Reset to default
        statusText.setSelection(0);    // Reset to default
    }



    public void onCancelBtnClick(View view){
        Intent intent = new Intent(CreateTask.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

}
