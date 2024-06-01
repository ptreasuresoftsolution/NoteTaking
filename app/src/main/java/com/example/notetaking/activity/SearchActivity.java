package com.example.notetaking.activity;

import android.os.Bundle;
import android.text.Spannable;
import android.util.Log;
import android.widget.SearchView;

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
import com.example.notetaking.function.NoteFolderAdapter;
import com.example.notetaking.function.SearchAdapter;
import com.example.notetaking.function.SessionPref;
import com.example.notetaking.modal.NoteFolder;
import com.example.notetaking.modal.NoteRow;
import com.example.notetaking.modal.NoteRowMaster;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private SearchAdapter adapter;
    private List<NoteRowMaster> dataList;
    private List<NoteRowMaster> filteredList;
    SessionPref sessionPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        sessionPref = new SessionPref(this);

        recyclerView = findViewById(R.id.recycler_view);
        searchView = findViewById(R.id.search_view);

        dataList = storeDataForSearch();
        filteredList = new ArrayList<>();

        adapter = new SearchAdapter(SearchActivity.this, dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private List<NoteRowMaster> storeDataForSearch() {
        CommonFunction.PleaseWaitShow(this);
        List<NoteRowMaster> noteRowMasters = new ArrayList<>();
        if (sessionPref.isSideOrganization()) { // read all collection
            CommonFunction.getCollectionReferenceForFolder().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            NoteFolder noteFolder = document.toObject(NoteFolder.class);
                            // DocumentSnapshot contains data
                            // You can access fields using methods like getString, getLong, etc.
                            for (String fldNm : noteFolder.folderTitles) {
                                // Do something with the field name and value
                                // For example, print them
                                Log.d("search add", fldNm);
                                CommonFunction.getCollectionReferenceForFolder().collection(fldNm).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            NoteRow noteRow = documentSnapshot.toObject(NoteRow.class);
                                            NoteRowMaster m = new NoteRowMaster();
                                            m.docId = documentSnapshot.getId();
                                            m.foldertitle = fldNm;
                                            m.attachAudio = noteRow.attachAudio;
                                            m.attachImg = noteRow.attachImg;
                                            m.content = noteRow.content;
                                            m.title = noteRow.title;
                                            m.timestamp = noteRow.timestamp;
                                            noteRowMasters.add(m);
                                        }
                                    }
                                }).addOnCompleteListener(task1 -> {
                                    CommonFunction.dismissDialog();
                                });
                            }
                        }
                    }
                }
            });
        } else {//only read user note folder/collection
            CommonFunction.getCollectionReferenceForFolder().collection("user_notes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        NoteRow noteRow = documentSnapshot.toObject(NoteRow.class);
                        NoteRowMaster m = new NoteRowMaster();
                        m.docId = documentSnapshot.getId();
                        m.foldertitle = "user_notes";
                        m.attachAudio = noteRow.attachAudio;
                        m.attachImg = noteRow.attachImg;
                        m.content = noteRow.content;
                        m.title = noteRow.title;
                        m.timestamp = noteRow.timestamp;
                        noteRowMasters.add(m);
                    }
                }
            }).addOnCompleteListener(task -> {
                CommonFunction.dismissDialog();
            });
        }
        return noteRowMasters;
    }

    private void filter(String query) {
        filteredList.clear();
        for (NoteRowMaster item : dataList) {
            if (item.foldertitle.contains(query.toLowerCase()) || item.title.contains(query.toLowerCase()) || CommonFunction.getContentFromJson(item.content).toString().contains(query.toLowerCase()) || CommonFunction.timestampToString(item.timestamp).contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter = new SearchAdapter(SearchActivity.this, filteredList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}