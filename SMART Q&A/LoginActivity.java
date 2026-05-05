package com.example.smartqa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        role  = getIntent().getStringExtra("role");

        // if role is null (coming from your old flow), default to student
        if (role == null) role = "student";

        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // hide tvResend since we removed email verification
        TextView tvResend = findViewById(R.id.tvResend);
        if (tvResend != null) tvResend.setVisibility(android.view.View.GONE);

        btnLogin.setOnClickListener(v -> loginUser());

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    private void loginUser() {

        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // validation
        if (email.isEmpty()) {
            etEmail.setError("Enter email");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Enter password");
            return;
        }

        // Firebase login — NO email verification check
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        // Save login state so app never asks again
                        SharedPreferences.Editor editor =
                                getSharedPreferences("SmartQA", MODE_PRIVATE).edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("role", role);
                        editor.putString("userEmail", email);
                        editor.apply();

                        Toast.makeText(this,
                                "Login successful!", Toast.LENGTH_SHORT).show();

                        goToDashboard();

                    } else {
                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Login failed";
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToDashboard() {
        if (role.equals("faculty")) {
            startActivity(new Intent(this, FacultyDashboardActivity.class));
        } else {
            startActivity(new Intent(this, StudentDashboardActivity.class));
        }
        finish();
    }
}
