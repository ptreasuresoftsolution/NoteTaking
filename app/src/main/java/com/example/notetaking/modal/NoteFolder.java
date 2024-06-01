package com.example.notetaking.modal;

import java.util.ArrayList;
import java.util.List;

public class NoteFolder {
    public List<String> folderTitles = new ArrayList<>();

    public List<String> getFolderTitles() {
        return folderTitles;
    }

    public void setFolderTitles(List<String> folder_titles) {
        this.folderTitles = folder_titles;
    }

}
