package com.example.smartqa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if already logged in — skip login screen
        SharedPreferences prefs = getSharedPreferences("SmartQA", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String  role       = prefs.getString("role", "");

        if (isLoggedIn && !role.isEmpty()) {
            // Go straight to dashboard — no login needed
            if (role.equals("faculty")) {
                startActivity(new Intent(this, FacultyDashboardActivity.class));
            } else {
                startActivity(new Intent(this, StudentDashboardActivity.class));
            }
            finish();
            return; // stop here
        }

        // First time — show role selection
        setContentView(R.layout.activity_main);

        Button btnStudent = findViewById(R.id.btnStudent);
        Button btnFaculty = findViewById(R.id.btnFaculty);

        btnStudent.setOnClickListener(v -> {
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("role", "student");
            startActivity(i);
        });

        btnFaculty.setOnClickListener(v -> {
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("role", "faculty");
            startActivity(i);
        });
    }
}   
