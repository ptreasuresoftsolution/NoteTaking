package com.example.notetaking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notetaking.R;
import com.example.notetaking.function.CommonFunction;
import com.example.notetaking.function.NoteAdapter;
import com.example.notetaking.function.NoteFolderAdapter;
import com.example.notetaking.modal.NoteFolder;
import com.example.notetaking.modal.NoteRow;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class NoteListActivity extends AppCompatActivity {
    String title;
    RecyclerView note_mainList;
    NoteAdapter noteAdapter;
    ImageView delete_fl_iv, add_note_iv;
    TextView folder_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        title = getIntent().getStringExtra("title");
        folder_name = findViewById(R.id.folder_name);
        folder_name.setText(title);
        note_mainList = findViewById(R.id.note_mainList);
        Query query = CommonFunction.getCollectionReferenceForFolder().collection(title).orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<NoteRow> options = new FirestoreRecyclerOptions.Builder<NoteRow>()
                .setQuery(query, NoteRow.class).build();
        note_mainList.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options, this, title);
        note_mainList.setAdapter(noteAdapter);
        delete_fl_iv = findViewById(R.id.delete_fl_iv);
        add_note_iv = findViewById(R.id.add_note_iv);
        add_note_iv.setOnClickListener(v -> {
            Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
            intent.putExtra("title", "");
            intent.putExtra("content", "");
            intent.putExtra("folder_title", title);
            startActivity(intent);
        });
        delete_fl_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunction.getCollectionReferenceForFolder().collection(title).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String docId = documentSnapshot.getId();
                            // Do something with the document ID
                            // For example, add it to a list
                            // or print it
                            CommonFunction.getCollectionReferenceForFolder().collection(title).document(docId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //note is deleted
                                        Log.e("done del docid", "Note deleted successfully");
                                    } else {
                                        Log.e("Error  del docid", "Failed while delete note");
                                    }
                                }
                            });
                        }
                    }
                });

                CommonFunction.getCollectionReferenceForFolder().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                NoteFolder noteFolder = document.toObject(NoteFolder.class);
                                noteFolder.folderTitles.remove(title);
                                CommonFunction.getCollectionReferenceForFolder().set(noteFolder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //note default folder add is added
                                            Toast.makeText(NoteListActivity.this, "Delete successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                            Log.e("done default", "Note saved successfully");
                                        } else {
                                            Log.e("Error default", "Failed while adding note");
                                        }
                                    }
                                });
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
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (noteAdapter != null) {
            noteAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (noteAdapter != null) {
            noteAdapter.notifyDataSetChanged();
        }
    }

}