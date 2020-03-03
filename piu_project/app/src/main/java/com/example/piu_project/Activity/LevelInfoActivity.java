package com.example.piu_project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.piu_project.R;
import com.example.piu_project.SongInfo;
import com.example.piu_project.UserInfo;
import com.example.piu_project.adapter.LevelInfoAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.piu_project.Util.INTENT_PATH;
import static com.example.piu_project.Util.showToast;

public class LevelInfoActivity extends BasicActivity {
    private static final String TAG = "LevelInfoActivity";
    private FirebaseFirestore firebaseFirestore;
    private LevelInfoAdapter levelInfoAdapter;
    private ArrayList<SongInfo> levelInfo;
    private boolean updating;
    private int setLevel;
    private String level;
    private String mode;
    private FirebaseUser user;
    private boolean topScrolled;
    private ImageView iv_profile;
    private String profilePath;
    private TextView tv_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levelinfo);
        setToolbarTitle("LEVEL");
        Intent intent = getIntent();
        level = intent.getStringExtra("setLevel");
        mode = intent.getStringExtra("setMode");
        tv_title = (TextView)findViewById(R.id.tv_title);

        iv_profile =(ImageView)(findViewById(R.id.iv_profile));
        findViewById(R.id.bt_photo).setOnClickListener(onClickListener);

        final int numberOfColumns = 3;
        firebaseFirestore = FirebaseFirestore.getInstance();
        levelInfo = new ArrayList<>();
        levelInfoAdapter = new LevelInfoAdapter(this, levelInfo,mode,level);

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setAdapter(levelInfoAdapter);



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();

                if(newState == 1 && firstVisibleItemPosition == 0){
                    topScrolled = true;
                }
                if(newState == 0 && topScrolled){
                    postsUpdate(true);
                    topScrolled = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();

                if(totalItemCount - 3 <= lastVisibleItemPosition && !updating){
                    postsUpdate(false);
                }

                if(0 < firstVisibleItemPosition){
                    topScrolled = false;
                }
            }
        });
        postsUpdate(false);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_photo:
                    myStartActivity(GalleryActivity.class);
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra(INTENT_PATH);
                    Glide.with(this).load(profilePath).centerCrop().override(500).into(iv_profile);
                    photoUploader();
                }
                break;
            }
        }
    }
    @Override
    public void onPause(){
        super.onPause();
    }

    private void photoUploader() {

//        loaderLayout.setVisibility(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        String title= tv_title.getText().toString();
        final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid()+"/" + mode+level+title+"/profile.jpg");
        if (profilePath != null) {
            try {
                InputStream stream = new FileInputStream(new File(profilePath));
                UploadTask uploadTask = mountainImagesRef.putStream(stream);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return mountainImagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            //infoUploader(profilePath);
                        } else {
                            showToast(LevelInfoActivity.this, "사진정보를 등록하는데 실패하였습니다.");
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                Log.e("로그", "에러: " + e.toString());
            }
        }
    }

    private void infoUploader(String photoUri) {
        UserInfo userInfo = new UserInfo("","","","",photoUri);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(LevelInfoActivity.this, "사진정보 등록을 성공하였습니다.");
//                        loaderLayout.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(LevelInfoActivity.this, "사진정보 등록에 실패하였습니다.");
//                        loaderLayout.setVisibility(View.GONE);
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }




    private void postsUpdate(final boolean clear) {
        updating = true;
        //Date date = userList.size() == 0 || clear ? new Date() : userList.get(userList.size() - 1).getCreatedAt();
        FirebaseDatabase database = FirebaseDatabase.getInstance();                      //Firebase database와 연동;
        DatabaseReference databaseReference_s = database.getReference("db_song");     //DB 테이블 연결
        databaseReference_s.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //firebase database의 data를 GET
                levelInfo.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    boolean isHere = false;
                    HashMap<String,String> h = (HashMap<String, String>)snapshot.getValue();
                    String[] level_s = h.get("level").split(String.valueOf(','));
                    for(int i=0;i<level_s.length; i++){
                        if(level_s[i].equals(mode+level)){
                            isHere = true;
                        }
                    }
                    if(isHere) {
                        levelInfo.add(new SongInfo(
                                h.get("album"),
                                h.get("artist"),
                                h.get("bpm"),
                                h.get("level"),
                                h.get("title"),
                                h.get("category")));
                    }
                }
                levelInfoAdapter.notifyDataSetChanged();                         //리스트 저장 및 새로고침
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //error 발생시
                Log.e("LevelInfoActivity", String.valueOf(databaseError.toException()));
            }
        });

//        CollectionReference collectionReference = firebaseFirestore.collection("song_db");
//        collectionReference.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            if(clear){
//                                levelInfo.clear();
//                            }
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                levelInfo.add(new SongInfo(
//                                        document.getData().get("album") == null ? null : document.getData().get("album").toString(),
//                                        document.getData().get("artist").toString(),
//                                        document.getData().get("bpm").toString(),
//                                        document.getData().get("level").toString(),
//                                        document.getData().get("title").toString()));
//                            }
//                            levelInfoAdapter.notifyDataSetChanged();
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                        updating = false;
//                    }
//                });
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}