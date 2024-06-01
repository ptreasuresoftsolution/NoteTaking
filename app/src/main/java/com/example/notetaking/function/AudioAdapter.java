package com.example.notetaking.function;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notetaking.R;

import java.io.IOException;
import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    private List<String> audioList;
    private MediaPlayer mediaPlayer;

    public AudioAdapter(List<String> audioList) {
        this.audioList = audioList;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_audio, parent, false);
        return new AudioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        String audio = audioList.get(position);
        holder.textViewAudio.setText(audio);
        holder.play_btn.setOnClickListener(v -> {
            String audioPath = audioList.get(position);
            playAudio(audioPath);
            if (holder.play_btn.getText().toString().equals("play"))
                holder.play_btn.setText("stop");
            else holder.play_btn.setText("play");
        });
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAudio;
        Button play_btn;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAudio = itemView.findViewById(R.id.textViewAudio);
            play_btn = itemView.findViewById(R.id.play_btn);
        }

    }

    private void playAudio(String audioPath) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}