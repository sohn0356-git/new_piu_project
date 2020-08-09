package com.example.piu_project.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.piu_project.R;
import com.example.piu_project.UserDetail;
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
    private ImageView iv_info;
    private TypedArray album_info;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);
        setToolbarTitle("곡정보");

        Resources resources = getResources();
        album_info = resources.obtainTypedArray(R.array.album);
        Intent intent = getIntent();
        String artist = intent.getStringExtra("artist");
        String title = intent.getStringExtra("title");
        String level = intent.getStringExtra("level");
        String bpm = intent.getStringExtra("bpm");
        int song_id = intent.getIntExtra("song_id",-1);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_artist = (TextView)findViewById(R.id.tv_artist);
        tv_bpm = (TextView)findViewById(R.id.tv_bpm);
        iv_info = (ImageView) findViewById(R.id.iv_info);
        profileImageVIew = findViewById(R.id.profileImageView);
        tv_artist.setText("아티스트 : "+artist);
        tv_title.setText("제목 : "+title);
        tv_bpm.setText("BPM : "+bpm);
        ArrayList<UserDetail> userDetail = new ArrayList<>();;
        List<Pair<String, String>> user_Achievement = new ArrayList<>();


//        if(!song_id.equals("")&&!(song_id==null)) {
//            Glide.with(this).load(album_info.getResourceId(Integer.parseInt(song_id), 0)).centerCrop().override(500).into(profileImageVIew);
//        }else{
//        int temp = intent.getIntExtra("song_id",-1);
//        String resName = "@drawable/a_"+String.valueOf(song_id);
//        String packName = this.getPackageName(); // 패키지명
//        int resID = getResources().getIdentifier(resName, "drawable", packName);
//

        String resName = "@drawable/a_"+String.valueOf(song_id);
        String packName = this.getPackageName(); // 패키지명
        int resID = this.getResources().getIdentifier(resName, "drawable", packName);

        profileImageVIew.setImageResource(resID);

//            Glide.with(this).load(album_info.getResourceId(temp-1,0)).centerCrop().override(500).into(profileImageVIew);
//        }
        String[] level_s = level.split(String.valueOf(','));
        for(int i=0;i<level_s.length; i++){
            user_Achievement.add(new Pair<>(level_s[i],"A"));
            String yLinkE = intent.getStringExtra(level_s[i]+"E");
            String yLinkJ = intent.getStringExtra(level_s[i]+"J");
            String yLinkN = intent.getStringExtra(level_s[i]+"N");
            userDetail.add(new UserDetail(level_s[i],"A",yLinkE,yLinkJ,yLinkN,""));
        }

        final int numberOfColumns = 8;
        RecyclerView recyclerView = findViewById(R.id.recyclerView_level);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        RecyclerView.Adapter mAdapter = new ShowInfoAdapter(this, userDetail,iv_info,title,resources);
        recyclerView.setAdapter(mAdapter);

    }
}