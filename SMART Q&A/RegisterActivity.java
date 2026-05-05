package com.example.smartqa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;
    private Button btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> registerUser());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 🧪 Validation
        if (email.isEmpty()) {
            etEmail.setError("Email required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password required");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Minimum 6 characters required");
            return;
        }

        // 🔥 Create user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {

                            // 📩 Send verification email (with success/failure feedback)
                            user.sendEmailVerification()
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(this,
                                                    "Verification email sent. Check Inbox/Spam.",
                                                    Toast.LENGTH_LONG).show()
                                    )
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this,
                                                    "Failed to send email: " + e.getMessage(),
                                                    Toast.LENGTH_LONG).show()
                                    );

                        } else {
                            Toast.makeText(this,
                                    "User created but not available. Try again.",
                                    Toast.LENGTH_LONG).show();
                        }

                        // 🚪 Sign out until verified
                        mAuth.signOut();

                        // ➡️ Go to Login screen
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();

                    } else {

                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Registration failed";

                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
