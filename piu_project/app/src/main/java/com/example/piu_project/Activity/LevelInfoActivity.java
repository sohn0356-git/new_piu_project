package com.example.piu_project.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.security.keystore.KeyGenParameterSpec;
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
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import static com.example.piu_project.Activity.MainActivity.allData;
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
    private String newLevel;
    private String category;
    private String mode;
    private FirebaseUser user;
    private boolean topScrolled;
    private ImageView iv_profile;
    private int testcnt;
    private ImageView iv_capture;
    private EditText et1;
    private EditText et2;
    private EditText et3;
    private EditText et4;
    private EditText et_search;
    private boolean userUpdate;
    private RecyclerView recyclerView;
    private Spinner spinner;
    private Spinner spinner_level;
    private Spinner spinner_left;
    private Spinner spinner_right;
    private RelativeLayout settingBackgroundLayout;
    private RelativeLayout searchingBackgroundLayout;
    private RadioButton rb_no, rb_on,rb_off;
    private ImageView iv_prev;
    private ImageView iv_next;
    private ImageView iv_reset;
    private ImageView iv_search;
    private Button bt_search;
    private Button bt_cancel;
    private String title;
    private String profilePath;
    private TextView tv_title;
    private int selected_idx,selected_idx_left,selected_idx_right;
    private int col_cnt;
    private final int numberOfColumns = 5;
    private int [] difficultyCount={0,0,0,0,0,0,0,0};
    private int [] S_song_id = {R.array.info_s01,R.array.info_s02,R.array.info_s03,R.array.info_s04,R.array.info_s05,R.array.info_s06,R.array.info_s07,R.array.info_s08,R.array.info_s09,R.array.info_s10,
                                R.array.info_s11,R.array.info_s12,R.array.info_s13,R.array.info_s14,R.array.info_s15,R.array.info_s16,R.array.info_s17,R.array.info_s18,R.array.info_s19,R.array.info_s20,
                                R.array.info_s21,R.array.info_s22,R.array.info_s23,R.array.info_s24,R.array.info_s25,R.array.info_s26,R.array.info_null,R.array.info_null};
    private int [] D_song_id = {R.array.info_null,R.array.info_null,R.array.info_d03,R.array.info_d04,R.array.info_d05,R.array.info_d06,R.array.info_d07,R.array.info_d08,R.array.info_d09,R.array.info_d10,
                                R.array.info_d11,R.array.info_d12,R.array.info_d13,R.array.info_d14,R.array.info_d15,R.array.info_d16,R.array.info_d17,R.array.info_d18,R.array.info_d19,R.array.info_d20,
                                R.array.info_d21,R.array.info_d22,R.array.info_d23,R.array.info_d24,R.array.info_d25,R.array.info_d26,R.array.info_d27,R.array.info_d28};
    private int [] SP_song_id = {R.array.info_sp01,R.array.info_sp02,R.array.info_sp03,R.array.info_sp04,R.array.info_sp05,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_sp10,
                                 R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_sp15,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,
                                 R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null};
    private int [] DP_song_id = {R.array.info_dp01,R.array.info_dp02,R.array.info_dp03,R.array.info_dp04,R.array.info_dp05,R.array.info_dp06,R.array.info_dp07,R.array.info_dp08,R.array.info_null,R.array.info_null,
                                 R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_dp20,
                                 R.array.info_null,R.array.info_null,R.array.info_dp23,R.array.info_dp24,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null};
    private int [] S_song_name = {R.array.name_s01,R.array.name_s02,R.array.name_s03,R.array.name_s04,R.array.name_s05,R.array.name_s06,R.array.name_s07,R.array.name_s08,R.array.name_s09,R.array.name_s10,
            R.array.name_s11,R.array.name_s12,R.array.name_s13,R.array.name_s14,R.array.name_s15,R.array.name_s16,R.array.name_s17,R.array.name_s18,R.array.name_s19,R.array.name_s20,
            R.array.name_s21,R.array.name_s22,R.array.name_s23,R.array.name_s24,R.array.name_s25,R.array.name_s26,R.array.info_null,R.array.info_null};
    private int [] D_song_name = {R.array.info_null,R.array.info_null,R.array.name_d03,R.array.name_d04,R.array.name_d05,R.array.name_d06,R.array.name_d07,R.array.name_d08,R.array.name_d09,R.array.name_d10,
            R.array.name_d11,R.array.name_d12,R.array.name_d13,R.array.name_d14,R.array.name_d15,R.array.name_d16,R.array.name_d17,R.array.name_d18,R.array.name_d19,R.array.name_d20,
            R.array.name_d21,R.array.name_d22,R.array.name_d23,R.array.name_d24,R.array.name_d25,R.array.name_d26,R.array.name_d27,R.array.name_d28};
    private int [] SP_song_name = {R.array.name_sp01,R.array.name_sp02,R.array.name_sp03,R.array.name_sp04,R.array.name_sp05,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.name_sp10,
            R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.name_sp15,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,
            R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null};
    private int [] DP_song_name = {R.array.name_dp01,R.array.name_dp02,R.array.name_dp03,R.array.name_dp04,R.array.name_dp05,R.array.name_dp06,R.array.name_dp07,R.array.name_dp08,R.array.info_null,R.array.info_null,
            R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.name_dp20,
            R.array.info_null,R.array.info_null,R.array.name_dp23,R.array.name_dp24,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null};
    private RelativeLayout loaderLayout;
    private FrameLayout frameLayout;
    private static final int WRITE_REQUEST_CODE = 101;
    public String[] song_number;
    public String[] song_name;
    private HashMap<String, String> userLevelList=new HashMap<>();;
    private String[] rank = {"SSS", "SS", "S", "A (Break on)", "A (Break off)", "B (Break on)", "B (Break off)", "C (Break on)", "C (Break off)", "D(Break on)", "D (Break off)", "F or Game Over", "No Play"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        selected_idx_left = 7;
        selected_idx_right = 0;
        super.onCreate(savedInstanceState);
        testcnt = 0;
        setContentView(R.layout.activity_levelinfo);
        isGrantStorage = grantExternalStoragePermission();
        Intent intent = getIntent();
        level = intent.getStringExtra("setLevel");
        mode = intent.getStringExtra("setMode");
        category = intent.getStringExtra("setCategory");
        setToolbarTitle("LEVEL_" + mode + "_" + level);
        if (mode.equals("S")) {
            song_number = getResources().getStringArray(S_song_id[Integer.parseInt(level) - 1]);
            song_name = getResources().getStringArray(S_song_name[Integer.parseInt(level) - 1]);
        } else if (mode.equals("D")) {
            song_number = getResources().getStringArray(D_song_id[Integer.parseInt(level) - 1]);
            song_name = getResources().getStringArray(D_song_name[Integer.parseInt(level) - 1]);
        } else if (mode.equals("SP")) {
            song_number = getResources().getStringArray(SP_song_id[Integer.parseInt(level) - 1]);
            song_name = getResources().getStringArray(SP_song_name[Integer.parseInt(level) - 1]);
        } else if (mode.equals("DP")) {
            song_number = getResources().getStringArray(DP_song_id[Integer.parseInt(level) - 1]);
            song_name = getResources().getStringArray(DP_song_name[Integer.parseInt(level) - 1]);
        }
        tv_title = (TextView) findViewById(R.id.tv_title);
        et1 = (EditText) findViewById(R.id.editText1);
        et2 = (EditText) findViewById(R.id.editText2);
        et3 = (EditText) findViewById(R.id.editText3);
        et4 = (EditText) findViewById(R.id.editText4);
        rb_no = (RadioButton) findViewById(R.id.rb_no);
        rb_on = (RadioButton) findViewById(R.id.rb_on);
        rb_off = (RadioButton) findViewById(R.id.rb_off);
        et_search = (EditText) findViewById(R.id.et_search);
        bt_search = (Button) findViewById(R.id.bt_search);
        bt_search.setOnClickListener(onClickListener);
        bt_cancel = (Button) findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(onClickListener);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setOnClickListener(onClickListener);
        iv_reset = (ImageView) findViewById(R.id.iv_reset);
        iv_reset.setOnClickListener(onClickListener);
        iv_prev = (ImageView) findViewById(R.id.iv_prev);
        iv_prev.setOnClickListener(onClickListener);
        if (level.equals("01")) {
            iv_prev.setVisibility(View.GONE);
        }
        iv_next = (ImageView) findViewById(R.id.iv_next);
        iv_next.setOnClickListener(onClickListener);
        if (level.equals("28")) {
            iv_next.setVisibility(View.GONE);
        }
        iv_capture = (ImageView) (findViewById(R.id.iv_capture));
        iv_capture.setOnClickListener(onClickListener);
        frameLayout = (FrameLayout) findViewById(R.id.container);
        iv_profile = (ImageView) (findViewById(R.id.iv_profile));
        iv_profile.setOnClickListener(onClickListener);
        settingBackgroundLayout = (findViewById(R.id.settingBackgroundLayout));
        searchingBackgroundLayout = (findViewById(R.id.searchingBackgroundLayout));
        searchingBackgroundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchingBackgroundLayout.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.bt_check).setOnClickListener(onClickListener);
        loaderLayout = (RelativeLayout) findViewById(R.id.loaderLyaout2);

        firebaseFirestore = FirebaseFirestore.getInstance();
        levelInfo = new ArrayList<>();
        levelInfoAdapter = new LevelInfoAdapter(this, levelInfo, mode, level, getResources(), settingBackgroundLayout);
        user = FirebaseAuth.getInstance().getCurrentUser();
        spinner_level = (Spinner) findViewById(R.id.spinner_level);
        spinner_left = (Spinner) findViewById(R.id.spinner_left);
        spinner_right = (Spinner) findViewById(R.id.spinner_right);


        // 스피너에 보여줄 문자열과 이미지 목록을 작성합니다.
        int[] spinnerImages = new int[]{R.drawable.rk_ts00, R.drawable.rk_ds00, R.drawable.rk_ss00, R.drawable.rk_an00, R.drawable.rk_af00, R.drawable.rk_bn00, R.drawable.rk_bf00,
                R.drawable.rk_cn00, R.drawable.rk_cf00, R.drawable.rk_dn00, R.drawable.rk_df00, R.drawable.rk_fn00, R.drawable.rk_ff00};
        int[] spinnerChoices = new int[]{R.drawable.rk_ts00, R.drawable.rk_ds00, R.drawable.rk_ss00, R.drawable.rk_an00, R.drawable.rk_bn00, R.drawable.rk_cn00,
                R.drawable.rk_dn00, R.drawable.rk_fn00, R.drawable.none};


        // 어댑터와 스피너를 연결합니다.

        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(LevelInfoActivity.this, spinnerImages);
        spinner_level.setAdapter(customSpinnerAdapter);
        CustomSpinnerAdapter customSpinnerLeftAdapter = new CustomSpinnerAdapter(LevelInfoActivity.this, spinnerChoices);
        spinner_left.setAdapter(customSpinnerLeftAdapter);
        CustomSpinnerAdapter customSpinnerRightAdapter = new CustomSpinnerAdapter(LevelInfoActivity.this, spinnerChoices);
        spinner_right.setAdapter(customSpinnerRightAdapter);
        spinner_left.setSelection(8);
        spinner_right.setSelection(0);
        // 스피너에서 아이템 선택시 호출하도록 합니다.

        spinner_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_idx = spinner_level.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_left.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_idx_left = spinner_left.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_right.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_idx_right = spinner_right.getSelectedItemPosition();
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
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();

                if (newState == 1 && firstVisibleItemPosition == 0) {
                    topScrolled = true;
                }
                if (newState == 0 && topScrolled) {
                    topScrolled = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();

                if (totalItemCount - 3 <= lastVisibleItemPosition && !updating) {
//                    for(int i=7;i>=0;i--) {
//                        postsUpdate(false,i);
//                    }
//                    postsUpdate(false,7);
                }

                if (0 < firstVisibleItemPosition) {
                    topScrolled = false;
                }
            }
        });
        userLevelListUpdate(true);

        //songInfoUpdate(false);
    }



    @Override
    public void onBackPressed() {
        if (settingBackgroundLayout.getVisibility() == View.VISIBLE) {
            settingBackgroundLayout.setVisibility(View.GONE);
        } else if (searchingBackgroundLayout.getVisibility() == View.VISIBLE) {
            searchingBackgroundLayout.setVisibility(View.GONE);
        } else {
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
//                    myStartActivity(GalleryActivity.class);
                    break;
                case R.id.iv_capture:
                    captureMyRecyclerView(recyclerView, 0, 0, recyclerView.getAdapter().getItemCount() - 1);

//                    captureMyRecyclerView(recyclerView, 0, 0, recyclerView.getAdapter().getItemCount() - 1);
                    break;
                case R.id.bt_check:
                    loaderLayout.setVisibility(View.VISIBLE);
                    photoUploader2();
//                    settingBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.iv_prev:
                    newLevel = String.valueOf(Integer.parseInt(level)-1);
                    if(newLevel.length()==1){
                        newLevel = "0"+newLevel;
                    }
                    myStartActivity(LevelInfoActivity.class);
                    finish();
                    break;
                case R.id.iv_next:
                    newLevel = String.valueOf(Integer.parseInt(level)+1);
                    if(newLevel.length()==1){
                        newLevel = "0"+newLevel;
                    }
                    myStartActivity(LevelInfoActivity.class);
                    finish();
                    break;
                case R.id.iv_search:
                    searchingBackgroundLayout.setVisibility(View.VISIBLE);
                    searchingBackgroundLayout.bringToFront();
                    break;
                case R.id.bt_search:

                    CharSequence charSequence="";
                    if(rb_no.isChecked()){
                        charSequence="n"+String.valueOf(selected_idx_left)+String.valueOf(selected_idx_right)+et_search.getText();
                    }else if(rb_on.isChecked()){
                        charSequence="o"+String.valueOf(selected_idx_left)+String.valueOf(selected_idx_right)+et_search.getText();
                    }else if(rb_off.isChecked()){
                        charSequence="f"+String.valueOf(selected_idx_left)+String.valueOf(selected_idx_right)+et_search.getText();
                    }
                    levelInfoAdapter.getFilter().filter(charSequence);
                    searchingBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.bt_cancel:
                    searchingBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.iv_reset:
                    et_search.setText("");
                    levelInfoAdapter.getFilter().filter("n80");
                    break;
            }
        }
    };
    private String saveToInternalStorage(Bitmap bitmapImage){
        String selectedOutputPath = "";
        String folderName="TEST";
        String imageName = mode+level;

            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folderName);
            // Create a storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("PhotoEditorSDK", "Failed to create directory");
                }
            }
            // Create a media file name
            selectedOutputPath = mediaStorageDir.getPath() + File.separator + imageName;
            Log.d("PhotoEditorSDK", "selected camera path " + selectedOutputPath);
            File file = new File(selectedOutputPath);
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 80, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        return selectedOutputPath;
    }
    public File getPrivateAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }

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

//        switch (requestCode) {
//            case 0: {
//                if (resultCode == Activity.RESULT_OK) {
//                    profilePath = data.getStringExtra(INTENT_PATH);
//                    Glide.with(this).load(profilePath).centerCrop().override(500).into(iv_profile);
//                }
//                break;
//            }
//        }


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
        userUpdate = true;
        userLevelList.clear();
        levelInfoAdapter.setRank(selected_idx);
        Log.d(TAG, "clear");
        loaderLayout.setVisibility(View.GONE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(user.getUid()).document(mode+level+title).set(achievementInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        profilePath="";
                        spinner_level.setSelection(0);
                        showToast(LevelInfoActivity.this, "정보 등록을 성공하였습니다.");
                        settingBackgroundLayout.setVisibility(View.GONE);
                        userLevelListUpdate(false);

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
    private void createFile() { // when you create document, you need to add Intent.ACTION_CREATE_DOCUMENT
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT); // filter to only show openable items.
        intent.addCategory(Intent.CATEGORY_OPENABLE); // Create a file with the requested Mime type
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, mode+level+".jpg");

        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5) {
            profilePath = data.getDataString();
//                    showToast(LevelInfoActivity.this,profilePath );
            Glide.with(this).load(data.getDataString()).override(500).into(iv_profile);
            Log.d("Picture Path", data.getDataString());
        }
        if (requestCode == WRITE_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (data != null && data.getData() != null) {
                        writeInFile(data.getData(), "bison is bision");
                    }
                    break;
                case Activity.RESULT_CANCELED:
                        break;

            }
        }
    }
    private void writeInFile(@NonNull Uri uri, @NonNull String text) {
        OutputStream outputStream;
        try {
            outputStream = getContentResolver().openOutputStream(uri);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(text);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            int constHeight = 0;
            for (int i = startPosition; i < endPosition + 1; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));

                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                if (i == 0) {
                    constHeight = holder.itemView.getMeasuredHeight();
                }
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth() / 5, constHeight);
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();

                if (bgColor != 0)
                    holder.itemView.setBackgroundColor(bgColor);
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }

                height += constHeight;

            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height / 5, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(Color.WHITE);
            int bitmapH = bitmaCache.get(String.valueOf(0)).getHeight();
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
                //디렉토리 없으면 생성
                File dir = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PIU_CAPTURE");
                if(!dir.exists()){
                    dir.mkdir();
                    if(!dir.exists()) {
                        showToast(LevelInfoActivity.this, "폴더생성 실패");
                    }
                }

                String string_path =dir.getAbsolutePath() + "/"+mode+level+".jpg";


                try{
                    FileOutputStream out = new FileOutputStream(string_path);
                    bigBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    showToast(LevelInfoActivity.this, "capture success");
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

    public boolean checkWritable() {
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

        for (int idx = 0; idx < song_number.length; idx++) {
            HashMap<String, Object> target = (HashMap<String, Object>) (allData.get(song_number[idx]));
            String song_id_tmp = String.valueOf(target.get("song_id"));
            String artist_tmp = (String) target.get("artist");
            String bpm_tmp = (String) target.get("bpm");
            String title_tmp = (String) target.get("title");
            String category_tmp = (String) target.get("category");
            String version_tmp = (String) target.get("version");
            String level_tmp = (String) (target.get("level"));
            HashMap<String, Object> youtubelink_tmp = (HashMap<String, Object>) (target.get("youtubeLink"));
            HashMap<String, String> stepmaker_tmp = (HashMap<String, String>) (target.get("stepmaker"));
            HashMap<String, String> unlockCondition_tmp = (HashMap<String, String>) (target.get("unlockCondition"));
            HashMap<String, String> detailDifficulty_tmp = (HashMap<String, String>) (target.get("detailDifficulty"));
            String dd = (String) detailDifficulty_tmp.get(mode + level);
            levelInfo.add(new SongInfo(
                    song_id_tmp,
                    artist_tmp,
                    title_tmp,
                    level_tmp,
                    bpm_tmp,
                    category_tmp,
                    version_tmp,
                    stepmaker_tmp,
                    youtubelink_tmp,
                    unlockCondition_tmp,
                    dd));
            if (!userLevelList.isEmpty()){
                String cur = mode + level + title_tmp;
                if (userLevelList.get(cur)!=null) {
                    levelInfo.get(levelInfo.size()-1).setUserLevel(userLevelList.get(cur));
                }
            }
        }

        Collections.sort(levelInfo, (Comparator<SongInfo>) (o1, o2) -> ((SongInfo)o2).getDifficulty().compareTo(((SongInfo)o1).getDifficulty()));
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
        //Date date = userList.size() == 0 || clear ? new Date() : userList.get(userList.size() - 1).getCreatedAt();
//        FirebaseDatabase database = FirebaseDatabase.getInstance();                      //Firebase database와 연동;
//        DatabaseReference databaseReference_s = database.getReference("db_song");     //DB 테이블 연결
//        databaseReference_s.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //firebase database의 data를 GET
//                if (_detailDifficulty == -1) {
////
//
//                    for (int j = 0; j < 8; j++) {
//                        difficultyCount[j] = 0;
//                    }
//                    for (int p = 0; p < levelInfo.size(); p++) {
//                        SongInfo songInfo = levelInfo.get(p);
//                        int selected_difficulty = Integer.parseInt(songInfo.getDifficulty());
//                        difficultyCount[selected_difficulty]++;
//                        if (p + 1 != levelInfo.size() && (!levelInfo.get(p + 1).getDifficulty().equals(songInfo.getDifficulty()) && difficultyCount[selected_difficulty] % 5 != 0)) {
//                            levelInfo.add(p + 1, new SongInfo(String.valueOf(selected_difficulty)));
//                        } else if (p + 1 == levelInfo.size()) {
//                            while (levelInfo.size() % 5 != 0) {
//                                levelInfo.add(p + 1, new SongInfo(String.valueOf(selected_difficulty)));
//                            }
//                        }
//                    }
//                    levelInfoAdapter.notifyDataSetChanged();
//                } else {
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Log.d(TAG+"test",snapshot.getValue().toString());
//
//                        boolean isHere = false;
//                        HashMap<String, String> h = (HashMap<String, String>) snapshot.getValue();
//                        String[] level_s = h.get("level").split(String.valueOf(','));
//                        for (int i = 0; i < level_s.length; i++) {
//                            if (level_s[i].equals(mode + level)) {
//                                isHere = true;
//                            }
//                        }
//                        if (isHere) {
//                            int difficulty = 0;
//                            HashMap<String, Object> h2 = (HashMap<String, Object>) snapshot.getValue();
//                            HashMap<String, Object> youtubelink = (HashMap<String, Object>)h2.get("youtubeLink");
//                            HashMap<String, String > stepmaker = (HashMap<String, String>)h2.get("stepmaker");
//
//
//                            HashMap<String, HashMap<String, String>> h3 = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
//                            HashMap<String, String> detailDifficulty = h3.get("detailDifficulty");
//                            Log.e("Msg", "error : " + h.get("title"));
////                            if(h.get("title").equals("트란사칼리아")) {
////                                Log.d("d","T");
////                            }
//                            difficulty = Integer.parseInt(detailDifficulty.get(mode + level));
//                            if (difficulty == _detailDifficulty) {
//                                col_cnt++;
//                                HashMap<String, Long> h4 = (HashMap<String, Long>) snapshot.getValue();
//                                String s = h4.get("song_id").toString();
//                                levelInfo.add(new SongInfo(
//                                        s,
//                                        h.get("artist"),
//                                        h.get("title"),
//                                        h.get("level"),
//                                        h.get("bpm"),
//                                        h.get("category"),
//                                        h.get("version"),
//                                        stepmaker,
//                                        youtubelink,
//                                        String.valueOf(difficulty)));
//                                if (!userLevelList.isEmpty()){
//                                    String cur = mode + level + h.get("title").toString();
//                                    if (userLevelList.get(cur)!=null) {
//                                        levelInfo.get(levelInfo.size()-1).setUserLevel(userLevelList.get(cur));
//                                    }
//                                }
//                                Log.d("LevelInfoActivity", mode + level + h.get("title").toString()+String.valueOf(userLevelList.size()));
//                                levelInfoAdapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
//                }//리스트 저장 및 새로고침
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                //error 발생시
//                Log.e("LevelInfoActivity", String.valueOf(databaseError.toException()));
//            }
//        });

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
        }
    }
    private void userLevelListUpdate(boolean init) {
        testcnt++;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean isHere = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String cur = document.getData().get("songInfo").toString();
                                if (cur.substring(0, 3).equals(mode + level)) {
                                    isHere = true;
                                    userLevelList.put(cur, document.getData().get("achievement").toString());
                                } else {
                                    if(isHere){
                                        break;
                                    }
                                }

                                Log.d(TAG, "testcnt : " + String.valueOf(testcnt) + document.getId() + " => " + document.getData()+"size : "+String.valueOf(userLevelList.size()));
                            }
                            if(init) {
                                postsUpdate(false, -1);
                            }else{
                                songInfoUpdate();
                            }
                        } else {
                            showToast(LevelInfoActivity.this, user.getUid().toString() + "reading fail");
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
//                            levelInfoAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.putExtra("setCategory",category);
        intent.putExtra("setLevel", newLevel);
        intent.putExtra("setMode", mode);
        startActivityForResult(intent, 1);
    }
}