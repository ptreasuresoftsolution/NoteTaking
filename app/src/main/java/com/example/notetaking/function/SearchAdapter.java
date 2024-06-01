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
import com.example.notetaking.modal.NoteRow;
import com.example.notetaking.modal.NoteRowMaster;
import com.google.gson.Gson;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    Context context;
    private List<NoteRowMaster> note;
    SessionPref sessionPref;

    public SearchAdapter(Context context, List<NoteRowMaster> dataList) {
        this.context = context;
        this.note = dataList;
        sessionPref = new SessionPref(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String title = note.get(position).title;
        if (sessionPref.isSideOrganization()) {
            title = "(" + note.get(position).foldertitle + ")" + title;
        }
        holder.title_tv.setText(title);
        holder.content_tv.setText(CommonFunction.getContentFromJson(note.get(position).content));
        holder.timestamp_tv.setText(CommonFunction.timestampToString(note.get(position).timestamp));

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(context, NoteActivity.class);
            intent.putExtra("title", note.get(position).title);
            intent.putExtra("content", note.get(position).content);
            intent.putExtra("folder_title", note.get(position).foldertitle);
            intent.putExtra("docId", note.get(position).docId);
            intent.putExtra("attachImg", new Gson().toJson(note.get(position).attachImg));
            intent.putExtra("attachAudio", new Gson().toJson(note.get(position).attachAudio));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return note.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title_tv, content_tv, timestamp_tv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.note_title_tv);
            content_tv = itemView.findViewById(R.id.note_content_tv);
            timestamp_tv = itemView.findViewById(R.id.note_timestamp_tv);
        }
    }
}