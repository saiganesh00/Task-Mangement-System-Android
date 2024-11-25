package com.example.android_app;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView logoImg = findViewById(R.id.logoImage);
        logoImg.setImageResource(R.drawable.logo);
        logoImg.setContentDescription("Logo Image");


        TextView logoTxt = findViewById(R.id.logoTextView);
        logoTxt.setText("TaskMaster");

        ImageView homeImg = findViewById(R.id.homePageImage);
        homeImg.setImageResource(R.drawable.home_image);

        TextView descTxt = findViewById(R.id.descriptionTextView);
        descTxt.setText("Manage Your Tasks With TaskMaster");

        Button homeBtn = findViewById(R.id.homeButton);
        homeBtn.setText("Let's Get Started");
        homeBtn.setContentDescription("Let's get Started");



    }
    public void onHomeButtonClick(View view){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}