package com.example.notetaking.modal;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class NoteRowMaster {
    public String foldertitle;
    public String docId;
    public String title;
    public String content;
    public Timestamp timestamp;
    public List<String> attachImg = new ArrayList<>();
    public List<String> attachAudio = new ArrayList<>();

    public NoteRowMaster() {
    }

}
