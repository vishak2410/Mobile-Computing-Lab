package com.example.smartqa;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QVH> {

    private List<Question> questions;
    private String currentUserId;
    private boolean isFaculty;
    private FirebaseFirestore db;

    public QuestionAdapter(List<Question> questions, String uid, boolean isFaculty) {
        this.questions     = questions;
        this.currentUserId = uid;
        this.isFaculty     = isFaculty;
        this.db            = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public QVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);
        return new QVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QVH h, int pos) {
        Question q = questions.get(pos);

        // Basic fields
        h.tvQuestion.setText(q.getText());
        h.tvSubject.setText("Subject: " + q.getSubject());
        h.tvAskedBy.setText(q.isAnonymous() ? "🔒 Anonymous" : "👤 " + q.getAskedBy());
        h.tvUpvotes.setText(String.valueOf(q.getUpvotes()));

        // Status + answer
        if (q.isAnswered()) {
            h.tvStatus.setText("✅ Answered");
            h.tvStatus.setTextColor(Color.parseColor("#27AE60"));
            h.tvAnswer.setVisibility(View.VISIBLE);
            h.tvAnswer.setText("💬 " + q.getAnswer());
        } else {
            h.tvStatus.setText("⏳ Pending");
            h.tvStatus.setTextColor(Color.parseColor("#E67E22"));
            h.tvAnswer.setVisibility(View.GONE);
        }

        // Upvote logic
        boolean voted = q.getUpvotedBy() != null
                && q.getUpvotedBy().contains(currentUserId);
        h.btnUpvote.setEnabled(!isFaculty);
        h.btnUpvote.setAlpha(voted ? 1.0f : 0.5f);

        if (!isFaculty) {
            h.btnUpvote.setOnClickListener(v -> {
                Map<String, Object> upd = new HashMap<>();
                if (voted) {
                    upd.put("upvotes",   q.getUpvotes() - 1);
                    upd.put("upvotedBy", FieldValue.arrayRemove(currentUserId));
                } else {
                    upd.put("upvotes",   q.getUpvotes() + 1);
                    upd.put("upvotedBy", FieldValue.arrayUnion(currentUserId));
                }
                db.collection("questions").document(q.getId()).update(upd);
            });
        }

        // Faculty — show Answer button
        if (isFaculty && !q.isAnswered()) {
            h.btnAnswer.setVisibility(View.VISIBLE);
            h.btnAnswer.setOnClickListener(v ->
                    showAnswerDialog(h.itemView.getContext(), q));
        } else {
            h.btnAnswer.setVisibility(View.GONE);
        }

        // ── DELETE BUTTON LOGIC ──────────────────────────────────────
        if (isFaculty) {
            // Faculty can delete ANY question
            h.btnDelete.setVisibility(View.VISIBLE);
            h.btnDelete.setOnClickListener(v ->
                    confirmDelete(h.itemView.getContext(), q));

        } else {
            // Student — ONLY show delete on their OWN questions
            // Must check: userId is not null AND matches exactly
            String qUserId = q.getUserId();

            if (qUserId != null
                    && !qUserId.isEmpty()
                    && qUserId.equals(currentUserId)) {

                h.btnDelete.setVisibility(View.VISIBLE);
                h.btnDelete.setOnClickListener(v ->
                        confirmDelete(h.itemView.getContext(), q));
            } else {
                // Someone else's question — completely hide the button
                h.btnDelete.setVisibility(View.GONE);
                h.btnDelete.setOnClickListener(null); // remove any old listener
            }
        }
// ──────────────────────────────────────────────────────────────────────────────────────────────
    }

    // ── Confirm before deleting ───────────────────────────────────────
    private void confirmDelete(Context ctx, Question q) {
        new AlertDialog.Builder(ctx)
                .setTitle("Delete Question")
                .setMessage("Are you sure you want to delete this question?")
                .setPositiveButton("Delete", (dialog, which) -> deleteQuestion(q))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteQuestion(Question q) {
        db.collection("questions")
                .document(q.getId())
                .delete();
        // Firestore snapshot listener auto-removes it from the list
    }

    // ── Faculty answer dialog ─────────────────────────────────────────
    private void showAnswerDialog(Context ctx, Question q) {
        EditText et = new EditText(ctx);
        et.setHint("Type your answer...");
        et.setPadding(40, 20, 40, 20);
        new AlertDialog.Builder(ctx)
                .setTitle("Answer this question")
                .setView(et)
                .setPositiveButton("Submit", (d, w) -> {
                    String ans = et.getText().toString().trim();
                    if (!ans.isEmpty()) {
                        Map<String, Object> upd = new HashMap<>();
                        upd.put("answer",   ans);
                        upd.put("answered", true);
                        upd.put("status",   "answered");
                        db.collection("questions")
                                .document(q.getId()).update(upd);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() { return questions.size(); }

    static class QVH extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvSubject, tvAskedBy,
                tvUpvotes, tvStatus, tvAnswer;
        Button btnUpvote, btnAnswer, btnDelete;

        QVH(@NonNull View v) {
            super(v);
            tvQuestion = v.findViewById(R.id.tvQuestion);
            tvSubject  = v.findViewById(R.id.tvSubject);
            tvAskedBy  = v.findViewById(R.id.tvAskedBy);
            tvUpvotes  = v.findViewById(R.id.tvUpvotes);
            tvStatus   = v.findViewById(R.id.tvStatus);
            tvAnswer   = v.findViewById(R.id.tvAnswer);
            btnUpvote  = v.findViewById(R.id.btnUpvote);
            btnAnswer  = v.findViewById(R.id.btnAnswer);
            btnDelete  = v.findViewById(R.id.btnDelete); // ← new
        }
    }
}
