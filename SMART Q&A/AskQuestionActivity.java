package com.example.smartqa;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;

public class AskQuestionActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EditText etQuestion;
    private CheckBox cbAnonymous;
    private Spinner spinnerSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);
        db    = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etQuestion     = findViewById(R.id.etQuestion);
        cbAnonymous    = findViewById(R.id.cbAnonymous);
        spinnerSubject = findViewById(R.id.spinnerSubject);

        String[] subjects = {"Mobile Computing","Cloud Technologies","Python Programming","NA","Prompt Engineering","Indian Constitution","Competitive Coding"};
        spinnerSubject.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, subjects));

        findViewById(R.id.btnSubmit).setOnClickListener(v -> submitQuestion());
    }

    private void submitQuestion() {
        String qText = etQuestion.getText().toString().trim();
        if (TextUtils.isEmpty(qText)) {
            Toast.makeText(this, "Enter your question", Toast.LENGTH_SHORT).show(); return;
        }
        boolean anon   = cbAnonymous.isChecked();
        String userId  = mAuth.getCurrentUser().getUid();
        String email   = mAuth.getCurrentUser().getEmail();
        String subject = spinnerSubject.getSelectedItem().toString();

        Map<String, Object> data = new HashMap<>();
        data.put("text",      qText);
        data.put("subject",   subject);
        data.put("anonymous", anon);
        data.put("askedBy",   anon ? "Anonymous" : email);
        data.put("userId",    userId);
        data.put("upvotes",   0);
        data.put("upvotedBy", new ArrayList<String>());
        data.put("answered",  false);
        data.put("answer",    "");
        data.put("status",    "pending");
        data.put("timestamp", new Date());

        db.collection("questions").add(data)
                .addOnSuccessListener(r -> { Toast.makeText(this, "Question submitted!", Toast.LENGTH_SHORT).show(); finish(); })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
