package com.example.notetaking.function;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notetaking.R;
import com.example.notetaking.activity.NoteActivity;
import com.example.notetaking.activity.NoteListActivity;
import com.example.notetaking.modal.NoteRow;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.gson.Gson;

import java.util.List;

public class NoteFolderAdapter extends RecyclerView.Adapter<NoteFolderAdapter.NoteViewHolder> {
    Context context;
    List<String> collections;

    public NoteFolderAdapter(Context context, List<String> collections) {
        this.context = context;
        this.collections = collections;
    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_folder_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.title_tv.setText(collections.get(position));

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(context, NoteListActivity.class);
            intent.putExtra("title", collections.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title_tv;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.note_title_tv);
        }
    }
}
