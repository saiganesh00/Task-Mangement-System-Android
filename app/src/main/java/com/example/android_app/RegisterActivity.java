package com.example.android_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.android_app.api.RegisterAPI;
import com.example.android_app.models.RegisterRequest;
import com.example.android_app.utils.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Adjust system bar padding for edge-to-edge support
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView logoImg = findViewById(R.id.registerImageLogo);
        logoImg.setImageResource(R.drawable.logo);

        TextView registerLogoText = findViewById(R.id.registerLogoText);
        registerLogoText.setText("TaskMaster");

        TextView createAcc = findViewById(R.id.createAccountText);
        createAcc.setText("Create Your Account");

        TextView emailText = findViewById(R.id.emailRegisterTextView);
        emailText.setText("Email");

        EditText emailField = findViewById(R.id.registerEmailField);
        emailField.setHint("Email");

        TextView passwordText = findViewById(R.id.registerPasswordTextView);
        passwordText.setText("Password");

        EditText passwordField = findViewById(R.id.registerPasswordField);
        passwordField.setHint("Password");

        TextView confirmPasswordText = findViewById(R.id.confirmPasswordTextView);
        confirmPasswordText.setText("Confirm Password");

        EditText confirmPasswordField = findViewById(R.id.confirmPasswordField);
        confirmPasswordField.setHint("Confirm Password");

        TextView registerBtn = findViewById(R.id.registerButton);
        registerBtn.setText("Sign Up");

        TextView continueText = findViewById(R.id.registerContinueTextView);
        continueText.setText("---------- or continue with ----------");

        TextView accText = findViewById(R.id.accountTextView);
        accText.setText("Already have an account ? ");

        TextView redirectText = findViewById(R.id.redirectToLoginTextView);
        redirectText.setText("Log In");
    }

    public void onLogoClick(View view) {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onRegisterBtnClick(View view) {
        EditText emailField = findViewById(R.id.registerEmailField);
        EditText passwordField = findViewById(R.id.registerPasswordField);
        EditText confirmPasswordField = findViewById(R.id.confirmPasswordField);

        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        // Validate input fields
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            if (email.isEmpty()) {
                emailField.setError("Email Required");
                emailField.requestFocus();
            } else if (password.isEmpty()) {
                passwordField.setError("Password Required");
                passwordField.requestFocus();
            } else {
                confirmPasswordField.setError("Confirm the password");
                confirmPasswordField.requestFocus();
            }
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordField.setError("Passwords do not match");
            confirmPasswordField.requestFocus();
            return;
        }

        // Create RegisterRequest object
        RegisterRequest registerRequest = new RegisterRequest(email, password, confirmPassword);

        // Get Retrofit instance from RetrofitInstance class
        Retrofit retrofit = RetrofitInstance.getRetrofitInstance();
        RegisterAPI registerAPI = retrofit.create(RegisterAPI.class);
        Call<Void> call = registerAPI.registerUser(registerRequest);

        // Enqueue the API call
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Registration successful
                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    // Optionally navigate to Login Activity
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();  // Close RegisterActivity after successful registration
                } else {
                    // Handle unsuccessful registration (e.g., show an error message)
                    Toast.makeText(RegisterActivity.this, "Registration Failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure (e.g., network issues)
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onLogInClick(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
