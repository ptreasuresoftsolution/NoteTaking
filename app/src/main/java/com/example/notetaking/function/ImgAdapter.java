package com.example.notetaking.function;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.notetaking.R;
import com.example.notetaking.activity.NoteActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ImgAdapter extends RecyclerView.Adapter<ImgAdapter.MyViewHolder> {
    private List<String> imageList;

    public ImgAdapter(List<String> imageList) {
        this.imageList = imageList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    @Override
    public ImgAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Glide.with(NoteActivity.context)
                .load(imageList.get(position))
                .into(holder.imageView);
        Log.e("file path","path:"+imageList.get(position));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}