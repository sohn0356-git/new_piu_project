package com.example.piu_project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.piu_project.AchievementInfo;
import com.example.piu_project.R;
import com.example.piu_project.SongInfo;
import com.example.piu_project.adapter.CustomSpinnerAdapter;
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

public class LevelInfoActivity extends BasicActivity implements TextWatcher {
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
    private EditText et1;
    private EditText et2;
    private EditText et3;
    private EditText et4;
    private Spinner spinner;
    private Spinner spinner_level;
    private RelativeLayout settingBackgroundLayout;
    private String title;
    private String profilePath;
    private TextView tv_title;
    private int selected_idx;
    private EditText ed_find;
    private int col_cnt;
    private final int numberOfColumns = 5;
    private String[] rank = {"SSS", "SS", "S", "A (Break on)", "A (Break off)", "B (Break on)", "B (Break off)", "C (Break on)", "C (Break off)", "D(Break on)", "D (Break off)", "F or Game Over", "No Play"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levelinfo);

        Intent intent = getIntent();
        level = intent.getStringExtra("setLevel");
        mode = intent.getStringExtra("setMode");
        setToolbarTitle("LEVEL_"+mode+"_"+level);
        tv_title = (TextView)findViewById(R.id.tv_title);
        et1 = (EditText)findViewById(R.id.editText1);
        et2 = (EditText)findViewById(R.id.editText2);
        et3 = (EditText)findViewById(R.id.editText3);
        et4 = (EditText)findViewById(R.id.editText4);
        iv_profile =(ImageView)(findViewById(R.id.iv_profile));
        iv_profile.setOnClickListener(onClickListener);
        settingBackgroundLayout = (findViewById(R.id.settingBackgroundLayout));
        findViewById(R.id.bt_check).setOnClickListener(onClickListener);
        ed_find = (EditText)findViewById(R.id.ed_find);
        ed_find.addTextChangedListener(this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        levelInfo = new ArrayList<>();
        levelInfoAdapter = new LevelInfoAdapter(this, levelInfo,mode,level,getResources(),settingBackgroundLayout);
        user = FirebaseAuth.getInstance().getCurrentUser();
        spinner_level = (Spinner)findViewById(R.id.spinner_level);


        // 스피너에 보여줄 문자열과 이미지 목록을 작성합니다.
       int[] spinnerImages = new int[]{R.drawable.rk_ts00,R.drawable.rk_ds00,R.drawable.rk_ss00,R.drawable.rk_an00,R.drawable.rk_af00,R.drawable.rk_bn00,R.drawable.rk_bf00,
                                        R.drawable.rk_cn00,R.drawable.rk_cf00,R.drawable.rk_dn00,R.drawable.rk_df00,R.drawable.rk_fn00,R.drawable.rk_ff00};


        // 어댑터와 스피너를 연결합니다.

        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(LevelInfoActivity.this, spinnerImages);
        spinner_level.setAdapter(customSpinnerAdapter);
        // 스피너에서 아이템 선택시 호출하도록 합니다.

        spinner_level = (Spinner)findViewById(R.id.spinner_level);
        spinner_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_idx = spinner_level.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
//                    for(int i=7;i>=0;i--) {
//                        postsUpdate(false,i);
//                    }
//                    postsUpdate(false,7);
                }

                if(0 < firstVisibleItemPosition){
                    topScrolled = false;
                }
            }
        });
        for(int i=7;i>=0;i--) {
            postsUpdate(false,i);
        }

        //songInfoUpdate(false);
    }



    @Override
    public void onBackPressed() {
        if(settingBackgroundLayout.getVisibility()==View.VISIBLE) {
            settingBackgroundLayout.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
            finish();
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_profile:
                    myStartActivity(GalleryActivity.class);
                    break;
                case R.id.bt_check:
                    photoUploader();
                    levelInfoAdapter.setRank(selected_idx);
                    settingBackgroundLayout.setVisibility(View.GONE);
                    break;
            }
        }
    };


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        levelInfoAdapter.getFilter().filter(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra(INTENT_PATH);
                    Glide.with(this).load(profilePath).centerCrop().override(500).into(iv_profile);
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

        title= tv_title.getText().toString();

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
                            AchievementInfo achievementInfo = new AchievementInfo(mode + level + title, String.valueOf(selected_idx), et1.getText().toString(), et2.getText().toString(), et3.getText().toString(), et4.getText().toString(), profilePath,user.getUid());
                            infoUploader(achievementInfo);
                        } else {
                            showToast(LevelInfoActivity.this, "정보를 등록하는데 실패하였습니다.");
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                Log.e("로그", "에러: " + e.toString());
            }
        }else{
            AchievementInfo achievementInfo = new AchievementInfo(mode + level + title,String.valueOf(selected_idx), et1.getText().toString(), et2.getText().toString(), et3.getText().toString(), et4.getText().toString(), "",user.getUid());
            infoUploader(achievementInfo);
        }
    }

    private void infoUploader(AchievementInfo achievementInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(user.getUid()).document(mode+level+title).set(achievementInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        showToast(LevelInfoActivity.this, "정보 등록을 성공하였습니다.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        showToast(LevelInfoActivity.this, "정보 등록에 실패하였습니다.");
                    }
                });
    }




    private void postsUpdate(final boolean clear, int _detailDifficulty) {
        updating = true;
        //Date date = userList.size() == 0 || clear ? new Date() : userList.get(userList.size() - 1).getCreatedAt();
        FirebaseDatabase database = FirebaseDatabase.getInstance();                      //Firebase database와 연동;
        DatabaseReference databaseReference_s = database.getReference("db_song");     //DB 테이블 연결
        databaseReference_s.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //firebase database의 data를 GET
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
                        int difficulty=0;
                        HashMap<String, HashMap<String, HashMap<String, String>>> h2 = (HashMap<String, HashMap<String, HashMap<String, String>>>) snapshot.getValue();
                        HashMap<String, HashMap<String, String>> youtubelink = h2.get("youtubeLink");
                        HashMap<String, HashMap<String, String>> stepmaker = h2.get("stepmaker");


                        HashMap<String, HashMap<String, String>> h3 =(HashMap<String, HashMap<String, String>>)snapshot.getValue();
                        HashMap<String, String> detailDifficulty = h3.get("detailDifficulty");
                        Log.e("Msg","error : "+h.get("title"));
                        difficulty = Integer.parseInt(detailDifficulty.get(mode+level));

                        if (difficulty == _detailDifficulty) {
                            col_cnt++;
                            HashMap<String, Long> h4 = (HashMap<String, Long>) snapshot.getValue();
                            String s = h4.get("song_id").toString();
                            levelInfo.add(new SongInfo(
                                    s,
                                    h.get("artist"),
                                    h.get("title"),
                                    h.get("level"),
                                    h.get("bpm"),
                                    h.get("category"),
                                    h.get("version"),
                                    stepmaker,
                                    youtubelink,
                                    String.valueOf(difficulty)));

                            songInfoUpdate(true);
                            levelInfoAdapter.notifyDataSetChanged();
                        }
                    }
                }                       //리스트 저장 및 새로고침
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //error 발생시
                Log.e("LevelInfoActivity", String.valueOf(databaseError.toException()));
            }
        });

//        CollectionReference collectionReference = firebaseFirestore.collection("user_info");
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
//                                document.getData().get("");
//                            }
//                            levelInfoAdapter.notifyDataSetChanged();
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
    }
    private void songInfoUpdate(final boolean clear) {
        updating = true;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                for (SongInfo songInfo : levelInfo) {
                                    if (document.getData().get("songInfo").toString().equals(mode+level + songInfo.getTitle())) {
                                        songInfo.setUserLevel(document.getData().get("achievement").toString());
                                        break;
                                    }
                                }
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        levelInfoAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}