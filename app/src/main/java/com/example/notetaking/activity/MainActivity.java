package com.example.notetaking.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notetaking.R;
import com.example.notetaking.function.CommonFunction;
import com.example.notetaking.function.NoteAdapter;
import com.example.notetaking.function.NoteFolderAdapter;
import com.example.notetaking.function.SessionPref;
import com.example.notetaking.modal.NoteFolder;
import com.example.notetaking.modal.NoteRow;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    SessionPref sessionPref;
    ImageView logout_iv, add_note_iv, add_folder_iv,search_iv;
    RecyclerView note_mainList;
    NoteAdapter noteAdapter;
    NoteFolderAdapter noteFolderAdapter;
    Switch switch_side;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        sessionPref = new SessionPref(this);

        switch_side = findViewById(R.id.switch_side);
        logout_iv = findViewById(R.id.logout_iv);
        add_folder_iv = findViewById(R.id.add_folder_iv);
        search_iv = findViewById(R.id.search_iv);
        search_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(i);
            }
        });
        add_note_iv = findViewById(R.id.add_note_iv);
        add_note_iv.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            intent.putExtra("title", "");
            intent.putExtra("content", "");
            intent.putExtra("folder_title", "user_notes");
            startActivity(intent);
        });
        add_folder_iv.setOnClickListener(v -> {
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.note_ttitle_dialog);
            EditText edit_text = dialog.findViewById(R.id.edit_text);
            Button go_button = dialog.findViewById(R.id.go_button);
            go_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, NoteListActivity.class);
                    intent.putExtra("title", edit_text.getText().toString());
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
            dialog.show();
        });
        logout_iv.setOnClickListener(v -> {
            logOut();
        });
        switch_side.setChecked(sessionPref.isSideOrganization());
        switch_side.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //enable organization
                sessionPref.setIsSideOrganization(true);
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            } else {
                //enable user
                sessionPref.setIsSideOrganization(false);
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }
        });
        note_mainList = findViewById(R.id.note_mainList);
        if (sessionPref.isSideOrganization()) {
            add_folder_iv.setVisibility(View.VISIBLE);
            add_note_iv.setVisibility(View.GONE);
        } else {
            add_note_iv.setVisibility(View.VISIBLE);
            add_folder_iv.setVisibility(View.GONE);
            Query query = CommonFunction.getCollectionReferenceForFolder().collection("user_notes").orderBy("timestamp", Query.Direction.DESCENDING);
            FirestoreRecyclerOptions<NoteRow> options = new FirestoreRecyclerOptions.Builder<NoteRow>()
                    .setQuery(query, NoteRow.class).build();
            note_mainList.setLayoutManager(new LinearLayoutManager(this));
            noteAdapter = new NoteAdapter(options, this,"user_notes");
            note_mainList.setAdapter(noteAdapter);
        }
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
        if (sessionPref.isSideOrganization()) {
            CommonFunction.getCollectionReferenceForFolder().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            NoteFolder noteFolder = document.toObject(NoteFolder.class);
                            if (document.getData().keySet().size() == 0) {
                                noteFolder.folderTitles.add("user_notes");
                                CommonFunction.getCollectionReferenceForFolder().set(noteFolder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //note default folder add is added
                                            Log.e("done default", "Note saved successfully");
                                        } else {
                                            Log.e("Error default", "Failed while adding note");
                                        }
                                    }
                                });
                            }
                            // DocumentSnapshot contains data
                            // You can access fields using methods like getString, getLong, etc.
                            for (String fieldName : noteFolder.folderTitles) {
                                // Do something with the field name and value
                                // For example, print them
                                Log.d("Field", fieldName);
                            }

                            note_mainList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            noteFolderAdapter = new NoteFolderAdapter(MainActivity.this, noteFolder.folderTitles);
                            note_mainList.setAdapter(noteFolderAdapter);
                        } else {
                            // Document does not exist
                            Log.d("Document", "No such document");
                        }
                    } else {
                        // Error getting document
                        Log.d("Error", "Error getting document: ", task.getException());
                    }
                }
            });
        }
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        sessionPref.logOutPref();
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }
}