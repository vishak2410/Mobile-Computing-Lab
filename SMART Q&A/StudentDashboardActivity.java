package com.example.smartqa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDashboardActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private QuestionAdapter adapter;
    private List<Question> questionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        db    = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Welcome text
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        String email = getSharedPreferences("SmartQA", MODE_PRIVATE)
                .getString("userEmail", "Student");
        tvWelcome.setText("Welcome, " + email);

        // RecyclerView
        RecyclerView rv = findViewById(R.id.rvQuestions);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuestionAdapter(
                questionList,
                mAuth.getCurrentUser().getUid(),
                false
        );
        rv.setAdapter(adapter);

        // Ask Question button
        findViewById(R.id.btnAskQuestion).setOnClickListener(v ->
                startActivity(new Intent(this, AskQuestionActivity.class))
        );

        // Logout
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            getSharedPreferences("SmartQA", MODE_PRIVATE)
                    .edit().clear().apply();
            mAuth.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        loadQuestions();
    }

    private void loadQuestions() {
        // ── Simple query with NO orderBy to avoid index errors ──
        db.collection("questions")
                .addSnapshotListener((snapshots, e) -> {

                    if (e != null) {
                        Log.e("StudentDash", "Error: " + e.getMessage());
                        return;
                    }
                    if (snapshots == null) return;

                    questionList.clear();

                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Question q = doc.toObject(Question.class);
                        if (q != null) {
                            q.setId(doc.getId());
                            questionList.add(q);
                        }
                    }

                    // Sort by upvotes in Java instead of Firestore
                    // This avoids needing a Firestore composite index
                    questionList.sort((a, b) ->
                            Integer.compare(b.getUpvotes(), a.getUpvotes()));

                    adapter.notifyDataSetChanged();
                });
    }
}
