package com.example.piu_project.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.piu_project.R;
import com.example.piu_project.adapter.GalleryAdapter;
import com.example.piu_project.adapter.ShowInfoAdapter;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.core.utilities.Pair;

public class ShowInfoActivity extends BasicActivity {
    private static final String TAG = "ShowInfoActivity";
    private ImageView profileImageVIew;
    private TextView tv_title;
    private TextView tv_artist;
    private TextView tv_bpm;
    private Button bt_info;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);
        setToolbarTitle("곡정보");


        Intent intent = getIntent();
        String artist = intent.getStringExtra("artist");
        String title = intent.getStringExtra("title");
        String level = intent.getStringExtra("level");
        String bpm = intent.getStringExtra("bpm");
        String album = intent.getStringExtra("album");
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_artist = (TextView)findViewById(R.id.tv_artist);
        tv_bpm = (TextView)findViewById(R.id.tv_bpm);
        bt_info = (Button)findViewById(R.id.bt_info);
        profileImageVIew = findViewById(R.id.profileImageView);
        tv_artist.setText("아티스트 : "+artist);
        tv_title.setText("제목 : "+title);
        tv_bpm.setText("BPM : "+bpm);
        List<Pair<String, String>> user_Achievement = new ArrayList<>();
        Glide.with(this).load(album).centerCrop().override(500).into(profileImageVIew);
        String[] level_s = level.split(String.valueOf(','));
        int[] num_s = new int[level_s.length];
        for(int i=0;i<level_s.length; i++){
            num_s[i] = Integer.parseInt(level_s[i]);
            user_Achievement.add(new Pair<>(level_s[i],"A"));
        }

        final int numberOfColumns = 8;
        RecyclerView recyclerView = findViewById(R.id.recyclerView_level);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        RecyclerView.Adapter mAdapter = new ShowInfoAdapter(this, user_Achievement,bt_info);
        recyclerView.setAdapter(mAdapter);
    }
}