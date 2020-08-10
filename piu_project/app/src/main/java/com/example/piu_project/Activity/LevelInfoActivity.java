package com.example.piu_project.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;

import static com.example.piu_project.Util.INTENT_PATH;
import static com.example.piu_project.Util.showToast;

public class LevelInfoActivity extends BasicActivity implements TextWatcher {
    private static final String TAG = "LevelInfoActivity";
    private static final String CAPTURE_PATH = "/CAPTURE_TEST/";
    private static boolean isGrantStorage;
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
    private ImageView iv_capture;
    private EditText et1;
    private EditText et2;
    private EditText et3;
    private EditText et4;
    private boolean userUpdate;
    private RecyclerView recyclerView;
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
    private int [] difficultyCount={0,0,0,0,0,0,0,0};
    private RelativeLayout loaderLayout;
    private FrameLayout frameLayout;
    private HashMap<String, String> userLevelList=new HashMap<>();;
    private String[] rank = {"SSS", "SS", "S", "A (Break on)", "A (Break off)", "B (Break on)", "B (Break off)", "C (Break on)", "C (Break off)", "D(Break on)", "D (Break off)", "F or Game Over", "No Play"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levelinfo);
        isGrantStorage = grantExternalStoragePermission();
        Intent intent = getIntent();
        level = intent.getStringExtra("setLevel");
        mode = intent.getStringExtra("setMode");
        setToolbarTitle("LEVEL_"+mode+"_"+level);
        tv_title = (TextView)findViewById(R.id.tv_title);
        et1 = (EditText)findViewById(R.id.editText1);
        et2 = (EditText)findViewById(R.id.editText2);
        et3 = (EditText)findViewById(R.id.editText3);
        et4 = (EditText)findViewById(R.id.editText4);
        iv_capture =(ImageView)(findViewById(R.id.iv_capture));
        iv_capture.setOnClickListener(onClickListener);
        frameLayout = (FrameLayout) findViewById(R.id.container);
        iv_profile =(ImageView)(findViewById(R.id.iv_profile));
        iv_profile.setOnClickListener(onClickListener);
        settingBackgroundLayout = (findViewById(R.id.settingBackgroundLayout));
        findViewById(R.id.bt_check).setOnClickListener(onClickListener);
        ed_find = (EditText)findViewById(R.id.ed_find);
        ed_find.addTextChangedListener(this);
        loaderLayout = (RelativeLayout)findViewById(R.id.loaderLyaout2);

        firebaseFirestore = FirebaseFirestore.getInstance();
        levelInfo = new ArrayList<>();
        levelInfoAdapter = new LevelInfoAdapter(this, levelInfo,mode,level,getResources(),settingBackgroundLayout);
        user = FirebaseAuth.getInstance().getCurrentUser();
        spinner_level = (Spinner)findViewById(R.id.spinner_level);
        userUpdate = true;
        userLevelListUpdate();

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

        recyclerView = findViewById(R.id.recyclerView);

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
        for(int i=7;i>=-1;i--) {
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
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // 외부 저장소에 있는 이미지 파일들에 대한 모든 정보를 얻을 수 있다.
                    intent.setType("image/*"); // image/* 형식의 Type 호출 -> 파일을 열 수 있는 앱들이 나열된다.
                    startActivityForResult(intent, 5);

//                    myStartActivity(PictureActivity.class);
//                    myStartActivity(GalleryActivity.class);
                    break;
                case R.id.iv_capture:
                    showToast(LevelInfoActivity.this, Environment.getExternalStorageDirectory().getAbsolutePath() + CAPTURE_PATH+"생성");
//                    captureRecyclerView(recyclerView,0);
                    showToast(LevelInfoActivity.this, "capture success");

                    captureMyRecyclerView(recyclerView, 0, 0, recyclerView.getAdapter().getItemCount() - 1);
                    break;
                case R.id.bt_check:
                    photoUploader2();
                    userLevelList.put(mode + level + title,String.valueOf(selected_idx));
                    loaderLayout.setVisibility(View.VISIBLE);
                    levelInfoAdapter.setRank(selected_idx);
//                    settingBackgroundLayout.setVisibility(View.GONE);
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
//    public void getPic(Uri imgUri) {
//        String imgPath = getRealPathFromURI(imgUri);
//        profilePath = imgPath;
//        // SharedPreferences 관리 Class : 해당 내용은 블로그에 있으니 참조
//
//    }
//    public String getRealPathFromURI(Uri uri) {
//        int index = 0; String[] proj = {MediaStore.Images.Media.DATA}; // 이미지 경로로 해당 이미지에 대한 정보를 가지고 있는 cursor 호출
//        Cursor cursor = getContentResolver().query(uri, proj, null, null, null); // 데이터가 있으면(가장 처음에 위치한 레코드를 가리킴)
//        if (cursor.moveToFirst()) { // 해당 필드의 인덱스를 반환하고, 존재하지 않을 경우 예외를 발생시킨다.
//            index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        }
//        Log.d("getRealPathFromURI", "getRealPathFromURI: " + cursor.getString(index));
//        return cursor.getString(index);
//    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 5) {
                    profilePath = data.getDataString();
//                    showToast(LevelInfoActivity.this,profilePath );
                    Glide.with(this).load(data.getDataString()).centerCrop().override(500).into(iv_profile);
                    Log.d("Picture Path", data.getDataString());
                }

        };
//        switch (requestCode) {
//            case 0: {
//                if (resultCode == Activity.RESULT_OK) {
//                    profilePath = data.getStringExtra(INTENT_PATH);
//                    Glide.with(this).load(profilePath).centerCrop().override(500).into(iv_profile);
//                }
//                break;
//            }
//        }
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
                        showToast(LevelInfoActivity.this, "정보를 수정중입니다.");
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
            showToast(LevelInfoActivity.this, "정보를 등록중입니다.");
            AchievementInfo achievementInfo = new AchievementInfo(mode + level + title,String.valueOf(selected_idx), et1.getText().toString(), et2.getText().toString(), et3.getText().toString(), et4.getText().toString(), "",user.getUid());
            infoUploader(achievementInfo);
        }
    }
    private void photoUploader2() {

//        loaderLayout.setVisibility(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        title = tv_title.getText().toString();

        AchievementInfo achievementInfo = new AchievementInfo(mode + level + title, String.valueOf(selected_idx), et1.getText().toString(), et2.getText().toString(), et3.getText().toString(), et4.getText().toString(), profilePath, user.getUid());
        infoUploader(achievementInfo);

    }

    private void infoUploader(AchievementInfo achievementInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(user.getUid()).document(mode+level+title).set(achievementInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        loaderLayout.setVisibility(View.GONE);
                        showToast(LevelInfoActivity.this, "정보 등록을 성공하였습니다.");
                        settingBackgroundLayout.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        loaderLayout.setVisibility(View.GONE);
                        showToast(LevelInfoActivity.this, "정보 등록에 실패하였습니다.");
                        settingBackgroundLayout.setVisibility(View.GONE);
                    }
                });
    }


    private void captureMyRecyclerView(RecyclerView view, int bgColor, int startPosition, int endPosition) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {

            if (startPosition > endPosition) {
                int tmp = endPosition;
                endPosition = startPosition;
                startPosition = tmp;
            }

            int size = endPosition - startPosition;
            int height = 0;
            Paint paint = new Paint();
            int iWidth = 0;
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = startPosition; i < endPosition + 1; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth() / 5, holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                if (bgColor != 0)
                    holder.itemView.setBackgroundColor(bgColor);
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }
                height += holder.itemView.getMeasuredHeight();

            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height / 5, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(Color.WHITE);
            int bitmapH =bitmaCache.get(String.valueOf(0)).getHeight();
            for (int i = 0; i < size + 1; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, iWidth, iHeight, paint);
                iWidth += bitmap.getWidth();
                if (i % 5 == 4) {
                    iHeight += bitmapH;
                    iWidth = 0;
                    bitmap.recycle();
                }
            }
            if (!checkWritable()) {
                //ask permission
                ActivityCompat.requestPermissions(LevelInfoActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                String ex_storage =Environment.getExternalStorageDirectory().getAbsolutePath(); //Get Absolute Path in External Sdcard
                String foler_name = "/"+CAPTURE_PATH+"/";
                String file_name = mode+level+".jpg";
                String string_path = ex_storage+foler_name;
                File file_path = new File(string_path);
                if(!file_path.isDirectory()){
                    file_path.mkdirs();
                    showToast(LevelInfoActivity.this, "폴더 생성.");
                }
                try{

                    FileOutputStream out = new FileOutputStream(string_path+file_name);
                    bigBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();
                }catch(FileNotFoundException exception){
                    showToast(LevelInfoActivity.this, "die1.");
                    Log.e("FileNotFoundException", exception.getMessage());
                }catch(IOException exception){
                    Log.e("IOException", exception.getMessage());
                    showToast(LevelInfoActivity.this, "die2.");
                }

            }
        }


    }

//    private void captureMyRecyclerView(RecyclerView view, int bgColor, int startPosition, int endPosition) {
//        if (isGrantStorage) {
//            RecyclerView.Adapter adapter = view.getAdapter();
//            Bitmap bigBitmap = null;
//            if (adapter != null) {
//
//                if (startPosition > endPosition) {
//                    int tmp = endPosition;
//                    endPosition = startPosition;
//                    startPosition = tmp;
//                }
//
//                int size = endPosition - startPosition;
//                int height = 0;
//                Paint paint = new Paint();
//                int iHeight = 0;
//                final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
//
//                final int cacheSize = maxMemory / 8;
//                LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
//                for (int i = startPosition; i < endPosition + 1; i++) {
//                    RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
//                    adapter.onBindViewHolder(holder, i);
//                    holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
//                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//                    holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
//                    holder.itemView.setDrawingCacheEnabled(true);
//                    holder.itemView.buildDrawingCache();
//                    if (bgColor != 0)
//                        holder.itemView.setBackgroundColor(bgColor);
//                    Bitmap drawingCache = holder.itemView.getDrawingCache();
//                    if (drawingCache != null) {
//
//                        bitmaCache.put(String.valueOf(i), drawingCache);
//                    }
//
//                    height += holder.itemView.getMeasuredHeight();
//                }
//
//                bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
//                Canvas bigCanvas = new Canvas(bigBitmap);
//                bigCanvas.drawColor(Color.WHITE);
//
//                for (int i = 0; i < size + 1; i++) {
//                    Bitmap bitmap = bitmaCache.get(String.valueOf(i));
//                    bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
//
//                    if(i%5==0){
//                        iHeight += bitmap.getHeight();
//                        bitmap.recycle();
//                    }
//                }
//
//            }
//
//        String strFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + CAPTURE_PATH;
////            String strFolderPath = Environment.DIRECTORY_DCIM + CAPTURE_PATH;
//            File folder = new File(strFolderPath);
//            if (!folder.exists()) {
//                folder.mkdirs();
//            }
//
//            String strFilePath = strFolderPath + "/" + System.currentTimeMillis() + ".png";
//            File fileCacheItem = new File(strFilePath);
//            OutputStream out = null;
//            try {
//                fileCacheItem.createNewFile();
//                out = new FileOutputStream(fileCacheItem);
//                bigBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//                Toast.makeText(this, "capture success", Toast.LENGTH_SHORT).show();
//            } catch (Exception e) {
//                Toast.makeText(this, "capture fail", Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            } finally {
//                try {
//                    out.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    public static boolean checkAvailable() {

        // Retrieving the external storage state
        String state = Environment.getExternalStorageState();

        // Check if available
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean checkWritable() {
        // Retrieving the external storage state
        String state = Environment.getExternalStorageState();

        // Check if writable
        if (Environment.MEDIA_MOUNTED.equals(state)){
            if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                return false;
            }else{
                return true;
            }
        }
        return false;
    }




    private boolean grantExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            }else{
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                return false;
            }
        }else{
            Toast.makeText(this, "External Storage Permission is Grant", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "External Storage Permission is Grant ");
            return true;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 23) {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                //resume tasks needing this permission
            }
        }
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
                    if (_detailDifficulty == -1) {
                        for (int j = 0; j < 8; j++) {
                            difficultyCount[j] = 0;
                        }
                        for (int p = 0; p < levelInfo.size(); p++) {
                            SongInfo songInfo = levelInfo.get(p);
                            int selected_difficulty = Integer.parseInt(songInfo.getDifficulty());
                            difficultyCount[selected_difficulty]++;
                            if (p + 1 != levelInfo.size() && (!levelInfo.get(p + 1).getDifficulty().equals(songInfo.getDifficulty()) && difficultyCount[selected_difficulty] % 5 != 0)) {
                                levelInfo.add(p + 1, new SongInfo(String.valueOf(selected_difficulty)));
                            } else if (p + 1 == levelInfo.size()) {
                                while (levelInfo.size() % 5 != 0) {
                                    levelInfo.add(p + 1, new SongInfo(String.valueOf(selected_difficulty)));
                                }
                            }
                        }
                        levelInfoAdapter.notifyDataSetChanged();
                    } else {
                        HashMap<String, String> h = (HashMap<String, String>) snapshot.getValue();
                        String[] level_s = h.get("level").split(String.valueOf(','));
                        for (int i = 0; i < level_s.length; i++) {
                            if (level_s[i].equals(mode + level)) {
                                isHere = true;
                            }
                        }
                        if (isHere) {
                            int difficulty = 0;
                            HashMap<String, HashMap<String, HashMap<String, String>>> h2 = (HashMap<String, HashMap<String, HashMap<String, String>>>) snapshot.getValue();
                            HashMap<String, HashMap<String, String>> youtubelink = h2.get("youtubeLink");
                            HashMap<String, HashMap<String, String>> stepmaker = h2.get("stepmaker");


                            HashMap<String, HashMap<String, String>> h3 = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                            HashMap<String, String> detailDifficulty = h3.get("detailDifficulty");
                            Log.e("Msg", "error : " + h.get("title"));
                            difficulty = Integer.parseInt(detailDifficulty.get(mode + level));

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
//                            songInfoUpdate();

                                levelInfoAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }//리스트 저장 및 새로고침
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
    private void songInfoUpdate() {
        if (!userLevelList.isEmpty()) {
            for (SongInfo songInfo : levelInfo) {
                String cur = mode + level + songInfo.getTitle();
                if (userLevelList.get(cur)!=null) {
                    songInfo.setUserLevel(userLevelList.get(cur));
                }
            }
        }else{
            userLevelListUpdate();
            if (!userLevelList.isEmpty()) {
                songInfoUpdate();
                showToast(LevelInfoActivity.this, "Sorry");
            }
        }
    }
    private void userLevelListUpdate() {
        if (userUpdate) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(user.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String cur = document.getData().get("songInfo").toString();
                                    if (cur.substring(0, 3).equals(mode + level)) {
                                        userLevelList.put(cur, document.getData().get("achievement").toString());
                                    }
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    userUpdate=false;
                                }
                            } else {
                                showToast(LevelInfoActivity.this, "reading fail");
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
//                            levelInfoAdapter.notifyDataSetChanged();
                        }
                    });


        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}