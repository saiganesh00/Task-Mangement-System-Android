package com.example.android_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.android_app.api.LoginAPI;
import com.example.android_app.models.LoginRequest;
import com.example.android_app.models.LoginResponse;
import com.example.android_app.utils.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Handle system bars insets for Edge to Edge layouts
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView loginLogo = findViewById(R.id.loginImageLogo);
        loginLogo.setImageResource(R.drawable.logo);

        TextView logoText = findViewById(R.id.loginLogoText);
        logoText.setText("TaskMaster");

        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome Back!");

        TextView emailText = findViewById(R.id.emailLoginTextView);
        emailText.setText("Email");

        EditText emailField = findViewById(R.id.loginEmailField);
        emailField.setHint("Email");

        TextView passwordText = findViewById(R.id.loginPasswordTextView);
        passwordText.setText("Password");

        EditText passwordField = findViewById(R.id.loginPasswordField);
        passwordField.setHint("Password");

        TextView loginBtn = findViewById(R.id.loginButton);
        loginBtn.setText("Log In");

        TextView ctnText = findViewById(R.id.loginContinueTextView);
        ctnText.setText("---------- or continue with ----------");

        TextView accountText = findViewById(R.id.accountTextView);
        accountText.setText("Don't have an Account ? ");

        TextView redirectToRegister = findViewById(R.id.redirectToRegisterTextView);
        redirectToRegister.setText("Sign Up");
    }

    public void onLogoClick(View view) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onLoginBtnClick(View view) {
        EditText emailField = findViewById(R.id.loginEmailField);
        EditText passwordField = findViewById(R.id.loginPasswordField);

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            if (email.isEmpty()) {
                emailField.setError("Email Required");
                emailField.requestFocus();
            } else {
                passwordField.setError("Password Required");
                passwordField.requestFocus();
            }
        } else {
            LoginRequest loginRequest = new LoginRequest(email, password);



            // Make API call to authenticate the user
            loginUser(loginRequest);
        }
    }

    private void loginUser(LoginRequest loginRequest) {
        LoginAPI loginAPI = RetrofitInstance.getRetrofitInstance().create(LoginAPI.class);
        Call<LoginResponse> call = loginAPI.loginUser(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getToken() != null) {
                        // Store token in SharedPreferences for persistence
                        String token = response.body().getToken();
                        String email = response.body().getEmail();
                        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("authToken", token);  // Save token in SharedPreferences
                        editor.putString("EMAIL", email);
                        editor.apply();

                        // Set token in RetrofitInstance for the session
                        RetrofitInstance.setAuthToken(token);
                        RetrofitInstance.setAuthPrefix("Token");
                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        intent.putExtra("EMAIL", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }





    public void onSignUpClick(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
