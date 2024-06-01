package com.example.notetaking.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notetaking.R;
import com.example.notetaking.function.CommonFunction;
import com.example.notetaking.function.NoteAdapter;
import com.example.notetaking.function.SessionPref;
import com.example.notetaking.modal.NoteRow;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    SessionPref sessionPref;
    ImageView logout_iv, add_note_iv;
    RecyclerView note_mainList;
    NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        sessionPref = new SessionPref(this);

        logout_iv = findViewById(R.id.logout_iv);
        add_note_iv = findViewById(R.id.add_note_iv);
        add_note_iv.setOnClickListener(v -> {
//            Dialog builder = new Dialog(MainActivity.this);
//            builder.setContentView(R.layout.note_ttitle_dialog);
//            EditText edit_text = builder.findViewById(R.id.edit_text);
//            Button go_button = builder.findViewById(R.id.go_button);
//
//            go_button.setOnClickListener(v1 -> {
//                if (edit_text.getText().toString().isEmpty()) {
//                    edit_text.setError("required");
//                    Toast.makeText(MainActivity.this, "Please enter note title", Toast.LENGTH_SHORT).show();
//                } else {
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            intent.putExtra("title", "");
            intent.putExtra("content", "");
            startActivity(intent);
//                }
//            });
//            builder.show();
        });
        logout_iv.setOnClickListener(v -> {
            logOut();
        });
        note_mainList = findViewById(R.id.note_mainList);
        Query query = CommonFunction.getCollectionReferenceForNotes().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<NoteRow> options = new FirestoreRecyclerOptions.Builder<NoteRow>()
                .setQuery(query, NoteRow.class).build();
//        if (options.getSnapshots().size() > 0) {
            note_mainList.setLayoutManager(new LinearLayoutManager(this));
            noteAdapter = new NoteAdapter(options, this);
            note_mainList.setAdapter(noteAdapter);
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sessionPref.logOutPref();
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        } else if (noteAdapter != null) {
            noteAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && noteAdapter != null) {
            noteAdapter.notifyDataSetChanged();
        }
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        sessionPref.logOutPref();
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }
}