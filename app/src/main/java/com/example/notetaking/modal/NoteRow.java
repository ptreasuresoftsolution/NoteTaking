package com.example.notetaking.modal;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class NoteRow {
    public String title;
    public String content;
    public Timestamp timestamp;
    public List<String> attachImg = new ArrayList<>();
    public List<String> attachAudio = new ArrayList<>();

    public NoteRow() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getAttachImg() {
        return attachImg;
    }

    public void setAttachImg(List<String> attachImg) {
        this.attachImg = attachImg;
    }

    public List<String> getAttachAudio() {
        return attachAudio;
    }

    public void setAttachAudio(List<String> attachAudio) {
        this.attachAudio = attachAudio;
    }
}
