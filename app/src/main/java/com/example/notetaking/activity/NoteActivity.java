package com.example.notetaking.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.notetaking.R;
import com.example.notetaking.activity.fragment.AttachmentFragment;
import com.example.notetaking.function.CommonFunction;
import com.example.notetaking.function.MyCallBack;
import com.example.notetaking.function.NoteFolderAdapter;
import com.example.notetaking.function.SessionPref;
import com.example.notetaking.modal.NoteFolder;
import com.example.notetaking.modal.NoteRow;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public static EditText title_et, content_et;
    ImageView btn_Bold, btn_Italic, btn_Bullet, btn_Save, btn_Delete, btn_attachment;
    public static boolean isEditMode = false;
    public static String title, content, docId, folder_title = "user_notes";
    public static List<String> attachAudio, attachImg;
    public static AppCompatActivity activity;
    public static Context context;
    public static SessionPref sessionPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        isEditMode = false;
        sessionPref = new SessionPref(NoteActivity.this);
        activity = NoteActivity.this;
        context = NoteActivity.this;

        title_et = findViewById(R.id.title_et);
        content_et = findViewById(R.id.content_et);
        btn_Bold = findViewById(R.id.btn_Bold);
        btn_Italic = findViewById(R.id.btn_Italic);
        btn_Bullet = findViewById(R.id.btn_Bullet);
        btn_Save = findViewById(R.id.btn_Save);
        btn_Delete = findViewById(R.id.btn_Delete);
        btn_attachment = findViewById(R.id.btn_attachment);

        //received for edit
        folder_title = getIntent().getStringExtra("folder_title");
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        attachImg = new ArrayList<>();
        String attachImgS = getIntent().getStringExtra("attachImg");
        if (attachImgS != null) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> arrayList = new Gson().fromJson(attachImgS, type);
//            Toast.makeText(context, "sz:" + arrayList.size(), Toast.LENGTH_SHORT).show();
            attachImg.addAll(arrayList);
        }

        attachAudio = new ArrayList<>();
        String attachAudioS = getIntent().getStringExtra("attachAudio");
        if (attachAudioS != null) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> arrayList = new Gson().fromJson(attachAudioS, type);
            attachAudio.addAll(arrayList);
        }

        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
        }
        if (isEditMode) {
            title_et.setText(title);
            content_et.setText(CommonFunction.getContentFromJson(content));
            btn_Delete.setVisibility(View.VISIBLE);
        }

        btn_Bold.setOnClickListener(v -> applyFormatting("bold"));

        btn_Italic.setOnClickListener(v -> applyFormatting("italic"));

        btn_Bullet.setOnClickListener(v -> applyFormatting("bullet"));

        btn_Save.setOnClickListener(v -> saveNote(null));
        btn_Delete.setOnClickListener(v -> deleteNote());

        btn_attachment.setOnClickListener(v -> saveNote(new MyCallBack() {
            @Override
            public void callBack() {
                //goto attachment
                CommonFunction.loadFragmentWithStack(NoteActivity.this, R.id.fragment_container_view_tag, new AttachmentFragment(), "AttachmentActivity");
            }
        }));
    }

    private void deleteNote() {
        if (isEditMode) {
            CommonFunction.getCollectionReferenceForFolder().collection(folder_title).document(docId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //note is deleted
                        Toast.makeText(NoteActivity.this, "Note deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(NoteActivity.this, "Failed while deleting note", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static void saveNote(MyCallBack myCallBack) {
        NoteRow note = new NoteRow();
        note.title = title_et.getText().toString();
        Spanned content = content_et.getText();
        note.content = CommonFunction.getContentAsJson(content);
        note.timestamp = Timestamp.now();
        note.attachImg = attachImg;
        note.attachAudio = attachAudio;
        Log.e("Content text", "text:" + SpannableString.valueOf(content).toString());

        // Save the note to your storage or database
        DocumentReference documentReference;
        if (isEditMode) {
            //update the note
            documentReference = CommonFunction.getCollectionReferenceForFolder().collection(folder_title).document(docId);
        } else {
            //create new note
            documentReference = CommonFunction.getCollectionReferenceForFolder().collection(folder_title).document();
        }
//        Toast.makeText(context, "doc id : " + documentReference.getId(), Toast.LENGTH_SHORT).show();
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
//                    Toast.makeText(context, "doc id 2: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                    if (!isEditMode) {
                        docId = documentReference.getId();
                        isEditMode = true;
                    }
                    storeFolderInList(folder_title);
                    if (myCallBack != null) {
                        myCallBack.callBack();
                    } else {
                        //note is added
                        Toast.makeText(context, "Note saved successfully", Toast.LENGTH_SHORT).show();
                        activity.finish();
                    }
                } else {
                    Toast.makeText(context, "Failed while adding note", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static void storeFolderInList(String folderTitle) {
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
                            boolean isNeed = true;
                            for (String fieldName : noteFolder.folderTitles) {
                                Log.d("Field", fieldName);
                                if (fieldName.equals(folder_title)) {
                                    isNeed = false;
                                }
                            }
                            if (isNeed) {
                                noteFolder.folderTitles.add(folder_title);
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

    private void applyFormatting(String formatting) {
        int start = content_et.getSelectionStart();
        int end = content_et.getSelectionEnd();
        Editable editable = content_et.getText();
        switch (formatting) {
            case "bold":
                editable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case "italic":
                editable.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case "bullet":
                editable.insert(start, "• ");
                break;
        }
    }
}