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

public class FacultyDashboardActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private QuestionAdapter adapter;
    private List<Question> questionList = new ArrayList<>();
    private String filterStatus = "all";

    // Keep reference to listener so we can remove it cleanly
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_dashboard);

        db    = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Welcome text
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        String email = getSharedPreferences("SmartQA", MODE_PRIVATE)
                .getString("userEmail", "Faculty");
        tvWelcome.setText("Welcome, " + email);

        // RecyclerView
        RecyclerView rv = findViewById(R.id.rvQuestions);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuestionAdapter(
                questionList,
                mAuth.getCurrentUser().getUid(),
                true
        );
        rv.setAdapter(adapter);

        // Filter buttons
        findViewById(R.id.btnFilterAll).setOnClickListener(v -> {
            filterStatus = "all";
            loadQuestions();
        });
        findViewById(R.id.btnFilterPending).setOnClickListener(v -> {
            filterStatus = "pending";
            loadQuestions();
        });
        findViewById(R.id.btnFilterAnswered).setOnClickListener(v -> {
            filterStatus = "answered";
            loadQuestions();
        });

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

        // Remove old listener before attaching new one
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        // Simple query — no orderBy, no index needed
        listenerRegistration = db.collection("questions")
                .addSnapshotListener((snapshots, e) -> {

                    if (e != null) {
                        Log.e("FacultyDash", "Error: " + e.getMessage());
                        return;
                    }
                    if (snapshots == null) return;

                    questionList.clear();

                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Question q = doc.toObject(Question.class);
                        if (q != null) {
                            q.setId(doc.getId());

                            // Apply filter in Java — not in Firestore query
                            switch (filterStatus) {
                                case "pending":
                                    if (!q.isAnswered())
                                        questionList.add(q);
                                    break;
                                case "answered":
                                    if (q.isAnswered())
                                        questionList.add(q);
                                    break;
                                default:
                                    questionList.add(q); // "all"
                                    break;
                            }
                        }
                    }

                    // Sort by upvotes descending in Java
                    questionList.sort((a, b) ->
                            Integer.compare(b.getUpvotes(), a.getUpvotes()));

                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up listener when activity closes
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
