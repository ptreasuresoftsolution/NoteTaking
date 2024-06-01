package com.example.notetaking.activity.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.notetaking.R;
import com.example.notetaking.activity.NoteActivity;
import com.example.notetaking.function.CommonFunction;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class AttachmentFragment extends Fragment {

    List<Fragment> fragments = new ArrayList<>();
    List<String> fragmentsTitle = new ArrayList<>();

    TabLayout tab_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attachment, container, false);

        tab_layout = view.findViewById(R.id.tab_layout);
        fragments.add(new ImgFragment());
        fragmentsTitle.add("Image");
        fragments.add(new AudioFragment());
        fragmentsTitle.add("Audio");

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = fragments.get(tab.getPosition());
                CommonFunction._LoadFirstFragment(NoteActivity.activity, R.id.container, fragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        CommonFunction._LoadFirstFragment(NoteActivity.activity, R.id.container, fragments.get(0));
        return view;
    }
}