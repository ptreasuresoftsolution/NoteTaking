package com.example.notetaking.activity.fragment;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.notetaking.R;
import com.example.notetaking.activity.NoteActivity;
import com.example.notetaking.function.AudioAdapter;
import com.example.notetaking.function.MyCallBack;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioFragment extends Fragment {
    RecyclerView note_audioList;
    Button mic_rec;
    private static final int REQUEST_PERMISSION_CODE = 1001;
    private MediaRecorder mediaRecorder;
    private String audioFilePath = null;
    private boolean isRecording = false;
    AudioAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio, container, false);
        note_audioList = view.findViewById(R.id.note_audioList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        note_audioList.setLayoutManager(layoutManager);
        adapter = new AudioAdapter(NoteActivity.attachAudio);
        note_audioList.setAdapter(adapter);

        mic_rec = view.findViewById(R.id.mic_rec);
        mic_rec.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        });
        return view;
    }

    private void startRecording() {
        audioFilePath = NoteActivity.context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/recording.3gp";
        File demoFile = new File(audioFilePath);
        try {
            if (demoFile.exists())
                demoFile.delete();
            demoFile.createNewFile();
        } catch (Exception e) {
            Log.e("recoding file", "refresh exception", e);
        }

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            mic_rec.setText("Stop");
            Toast.makeText(getContext(), "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("audio frag", "prepare() failed", e);
        }
    }

    private void saveToLocalStorage() {
        if (audioFilePath != null) {
            File directory = NoteActivity.context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "Recording_" + timeStamp + ".3gp";
            File outputFile = new File(directory, fileName);
            progressDialog = new ProgressDialog(NoteActivity.context);
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            try {
                File inputFile = new File(audioFilePath);
                if (inputFile.exists()) {
                    if (inputFile.renameTo(outputFile)) {
                        Toast.makeText(getContext(), "File saved to local storage", Toast.LENGTH_SHORT).show();

                        //outputFile is upload firebase storage
                        Uri file = Uri.fromFile(outputFile);
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                        StorageReference riversRef = storageRef.child("audio/" + file.getLastPathSegment());
                        UploadTask uploadTask = riversRef.putFile(file);

                        progressDialog.show();
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Fail upload on firebase", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                // ...
                                progressDialog.dismiss();
                                riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String downloadUrl = uri.toString();
                                    Log.d("TAG Img fragment", "Download URL: " + downloadUrl);

                                    NoteActivity.attachAudio.add(downloadUrl);
                                    NoteActivity.saveNote(new MyCallBack() {
                                        @Override
                                        public void callBack() {
                                            Toast.makeText(getContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }).addOnFailureListener(exception -> {
                                    // Handle any errors getting the download URL
                                    Toast.makeText(getContext(), "Failed to get download URL: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            }
                        }).addOnProgressListener(taskSnapshot -> {
                            // Calculate progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            Log.d("Img Upload frag TAG", "Upload is " + progress + "% done");
                            // You can update UI with the upload progress here if needed
                            progressDialog.setProgress((int) progress);
                        });
                    } else {

                        Toast.makeText(getContext(), "Failed to save file to local storage", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Input file does not exist", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error saving file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Audio file path is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            mic_rec.setText("Start");
            Toast.makeText(getContext(), "Recording stopped", Toast.LENGTH_SHORT).show();

            // Save to storage
            saveToLocalStorage();
        }
    }

    private ProgressDialog progressDialog;
}