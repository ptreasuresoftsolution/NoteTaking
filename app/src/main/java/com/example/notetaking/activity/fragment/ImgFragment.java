package com.example.notetaking.activity.fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.notetaking.R;
import com.example.notetaking.activity.MainActivity;
import com.example.notetaking.activity.NoteActivity;
import com.example.notetaking.function.ImgAdapter;
import com.example.notetaking.function.MyCallBack;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class ImgFragment extends Fragment {
    RecyclerView note_imgList;
    ImageButton img_add;
    ActivityResultLauncher activityResultLauncherForImg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_img, container, false);
        activityResultLauncherForImg = registerForActivityResult(new ActivityResultContracts.GetContent(), photoUri -> {
            Uri file = photoUri;
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference riversRef = storageRef.child("images/" + file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);

            progressDialog = new ProgressDialog(NoteActivity.context);
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            // Register observers to listen for when the download is done or if it fails
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

                        NoteActivity.attachImg.add(downloadUrl);
                        NoteActivity.saveNote(new MyCallBack() {
                            @Override
                            public void callBack() {
                                Toast.makeText(getContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                imgAdapter.notifyDataSetChanged();
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
        });

        note_imgList = view.findViewById(R.id.note_imgList);
        note_imgList.setLayoutManager(new GridLayoutManager(NoteActivity.context, 2));
//        Toast.makeText(getContext(), "size: " + NoteActivity.attachImg, Toast.LENGTH_SHORT).show();
        imgAdapter = new ImgAdapter(NoteActivity.attachImg);
        note_imgList.setAdapter(imgAdapter);

        img_add = view.findViewById(R.id.img_add);
        img_add.setOnClickListener(v -> {
            activityResultLauncherForImg.launch(("image/*"));
        });
        return view;
    }

    private ProgressDialog progressDialog;
    ImgAdapter imgAdapter;
}