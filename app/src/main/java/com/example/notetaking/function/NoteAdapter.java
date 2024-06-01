package com.example.notetaking.function;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notetaking.R;
import com.example.notetaking.activity.NoteActivity;
import com.example.notetaking.modal.NoteRow;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.gson.Gson;

import java.util.Arrays;

public class NoteAdapter extends FirestoreRecyclerAdapter<NoteRow, NoteAdapter.NoteViewHolder> {
    Context context;

    public NoteAdapter(@NonNull FirestoreRecyclerOptions<NoteRow> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull NoteRow note) {
        holder.title_tv.setText(note.title);
        holder.content_tv.setText(CommonFunction.getContentFromJson(note.content));
        holder.timestamp_tv.setText(CommonFunction.timestampToString(note.timestamp));

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(context, NoteActivity.class);
            intent.putExtra("title", note.title);
            intent.putExtra("content", note.content);
            String docId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            intent.putExtra("attachImg", new Gson().toJson(note.attachImg));
            intent.putExtra("attachAudio", new Gson().toJson(note.attachAudio));
            context.startActivity(intent);
        });

    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note_item, parent, false);
        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title_tv, content_tv, timestamp_tv;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.note_title_tv);
            content_tv = itemView.findViewById(R.id.note_content_tv);
            timestamp_tv = itemView.findViewById(R.id.note_timestamp_tv);
        }
    }
}
