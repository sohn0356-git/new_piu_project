package com.example.piu_project.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
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
import androidx.core.content.FileProvider;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedOutputStream;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import static com.example.piu_project.Activity.MainActivity.allData;
import static com.example.piu_project.Activity.MainActivity.userData;
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
//    private String category;
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
    private String profilePath="";
    private TextView tv_title;
    private int selected_idx,selected_idx_left,selected_idx_right;
    private int col_cnt;
    private TextView tv_position;
    private final int numberOfColumns = 5;
    private FrameLayout fl_cancel;
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
    private int [] COOP_song_id = {R.array.info_null,R.array.info_coop02,R.array.info_coop03,R.array.info_coop04,R.array.info_coop05,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,
                                 R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,
                                 R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null};
    private int [] S_song_name = {R.array.name_S01,R.array.name_S02,R.array.name_S03,R.array.name_S04,R.array.name_S05,R.array.name_S06,R.array.name_S07,R.array.name_S08,R.array.name_S09,R.array.name_S10,
            R.array.name_S11,R.array.name_S12,R.array.name_S13,R.array.name_S14,R.array.name_S15,R.array.name_S16,R.array.name_S17,R.array.name_S18,R.array.name_S19,R.array.name_S20,
            R.array.name_S21,R.array.name_S22,R.array.name_S23,R.array.name_S24,R.array.name_S25,R.array.name_S26,R.array.info_null,R.array.info_null};
    private int [] D_song_name = {R.array.info_null,R.array.info_null,R.array.name_D03,R.array.name_D04,R.array.name_D05,R.array.name_D06,R.array.name_D07,R.array.name_D08,R.array.name_D09,R.array.name_D10,
            R.array.name_D11,R.array.name_D12,R.array.name_D13,R.array.name_D14,R.array.name_D15,R.array.name_D16,R.array.name_D17,R.array.name_D18,R.array.name_D19,R.array.name_D20,
            R.array.name_D21,R.array.name_D22,R.array.name_D23,R.array.name_D24,R.array.name_D25,R.array.name_D26,R.array.name_D27,R.array.name_D28};
    private int [] SP_song_name = {R.array.name_SP01,R.array.name_SP02,R.array.name_SP03,R.array.name_SP04,R.array.name_SP05,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.name_SP10,
            R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.name_SP15,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,
            R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null};
    private int [] DP_song_name = {R.array.name_DP01,R.array.name_DP02,R.array.name_DP03,R.array.name_DP04,R.array.name_DP05,R.array.name_DP06,R.array.name_DP07,R.array.name_DP08,R.array.info_null,R.array.info_null,
            R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.name_DP20,
            R.array.info_null,R.array.info_null,R.array.name_DP23,R.array.name_DP24,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null};
    private int [] COOP_song_name = {R.array.info_null,R.array.name_Coop02,R.array.name_Coop03,R.array.name_Coop04,R.array.name_Coop05,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,
            R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,
            R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null,R.array.info_null};
    private RelativeLayout loaderLayout;
    private RelativeLayout loaderLayout2;
    private FrameLayout frameLayout;
    public static final int TAKE_FROM_GALLERY = 100;
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
        setToolbarTitle("LEVEL_" + mode + "_" + level);

//        category = intent.getStringExtra("setCategory");

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
        } else {
            song_number = getResources().getStringArray(COOP_song_id[Integer.parseInt(level) - 1]);
            song_name = getResources().getStringArray(COOP_song_name[Integer.parseInt(level) - 1]);
        }
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_position = (TextView)findViewById(R.id.tv_position);
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
        fl_cancel = (FrameLayout)findViewById(R.id.fl_cancel);
        fl_cancel.setOnClickListener(onClickListener);
        findViewById(R.id.bt_delete).setOnClickListener(onClickListener);
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
        loaderLayout = (RelativeLayout) findViewById(R.id.loaderLyaout);
        loaderLayout2 = (RelativeLayout) findViewById(R.id.loaderLyaout2);

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
    private void infoUpdate(String title) {
        //Date date = userList.size() == 0 || clear ? new Date() : userList.get(userList.size() - 1).getCreatedAt();
        if(userData.get(mode+level+title)!=null) {
            et1.setText(((HashMap<String,String>)userData.get(mode+level+title)).get("n_Great"));
            et2.setText(((HashMap<String,String>)userData.get(mode+level+title)).get("n_Good"));
            et3.setText(((HashMap<String,String>)userData.get(mode+level+title)).get("n_Bad"));
            et4.setText(((HashMap<String,String>)userData.get(mode+level+title)).get("n_Miss"));
            String path = ((HashMap<String,String>)userData.get(mode+level+title)).get("photoUrl");
            if (path.equals("")) {
                Glide.with(this).load(R.drawable.ic_up00).centerInside().override(500).into(iv_profile);
            } else {
//                            showToast(activity,document.get("photoUrl").toString() );
                Glide.with(this).load(path).override(500).into(iv_profile);
//                            Glide.with(activity).load(document.get("photoUrl").toString()).centerCrop().override(500).into(iv_profile);
            }
            loaderLayout.setVisibility(View.GONE);
            Log.d(TAG, "DocumentSnapshot data: " + path);
        } else {
            loaderLayout.setVisibility(View.GONE);
            Glide.with(this).load(R.drawable.ic_up00).centerInside().override(500).into(iv_profile);
            Log.d(TAG, "No such document");
        }
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
                    title = tv_title.getText().toString();
                    takeImage();
//                    Intent intent = new Intent(Intent.ACTION_PICK);
//                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    // 외부 저장소에 있는 이미지 파일들에 대한 모든 정보를 얻을 수 있다.
//                    intent.setType("image/*"); // image/* 형식의 Type 호출 -> 파일을 열 수 있는 앱들이 나열된다.
//                    startActivityForResult(intent, 5);
//                    myStartActivity(GalleryActivity.class);
                    break;
                case R.id.iv_capture:
                    captureMyRecyclerView(recyclerView, 0, 0, recyclerView.getAdapter().getItemCount() - 1);

//                    captureMyRecyclerView(recyclerView, 0, 0, recyclerView.getAdapter().getItemCount() - 1);
                    break;
                case R.id.fl_cancel:
                    searchingBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.bt_delete:
                    title= tv_title.getText().toString();
                    infoDelete(title);
                    levelInfoAdapter.setRank(-1);
                    levelInfo.get(Integer.parseInt(tv_position.getText().toString())).setUserLevel("");
                    if(((HashMap<String,String>)userData.get(mode+level+title))!=null) {
                        String filePath = ((HashMap<String, String>) userData.get(mode + level + title)).get("photoUrl");
                        if (!filePath.equals("")) {
                            File f = new File(filePath);
                            if (f.delete()) {
                                showToast(LevelInfoActivity.this, "성공적으로 삭제되었습니다.");
                            } else {
                                showToast(LevelInfoActivity.this, "삭제를 실패했습니다.");
                            }
                        }
                    }
                    userData.remove(mode+level+title);
                    settingBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.bt_check:
                    loaderLayout2.setVisibility(View.VISIBLE);
                    title= tv_title.getText().toString();
                    if(!profilePath.equals("")) {
                        String save_folder = getString(R.string.app_name);
                        String fileName = "/" + mode + level + title + ".png";
                        String externalPath = Environment.getExternalStorageDirectory().getAbsolutePath() + '/' + save_folder;
                        File dirFile = new File(externalPath);
                        if (!dirFile.isDirectory()) {
                            dirFile.mkdirs();
                        }
                        String save_path = externalPath + fileName;
//                showToast(LevelInfoActivity.this,save_path);
                        copyFile(profilePath, save_path);
                        profilePath = save_path;
                    } else if(((HashMap<String,String>)userData.get(mode+level+title))!=null) {
                        profilePath = ((HashMap<String, String>) userData.get(mode + level + title)).get("photoUrl");
                    }
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
    private void takeImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, TAKE_FROM_GALLERY);
    }
    private boolean copyFile(String strSrc , String save_file){
        File file = new File(strSrc);

        boolean result;
        if(file!=null&&file.exists()){

            try {

                FileInputStream fis = new FileInputStream(file);
                FileOutputStream newfos = new FileOutputStream(save_file);
                int readcount=0;
                byte[] buffer = new byte[1024];

                while((readcount = fis.read(buffer,0,1024))!= -1){
                    newfos.write(buffer,0,readcount);
                }
                newfos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            result = true;
        }else{
            result = false;
        }
        return result;
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
    public String getRealPathFromURI(Uri uri) {
        int index = 0; String[] proj = {MediaStore.Images.Media.DATA}; // 이미지 경로로 해당 이미지에 대한 정보를 가지고 있는 cursor 호출
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null); // 데이터가 있으면(가장 처음에 위치한 레코드를 가리킴)
        if (cursor.moveToFirst()) { // 해당 필드의 인덱스를 반환하고, 존재하지 않을 경우 예외를 발생시킨다.
            index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        Log.d("getRealPathFromURI", "getRealPathFromURI: " + cursor.getString(index));
        return cursor.getString(index);
    }

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
            AchievementInfo achievementInfo = new AchievementInfo(mode + level + title,String.valueOf(selected_idx), et1.getText().toString(), et2.getText().toString(), et3.getText().toString(), et4.getText().toString(), "",user.getEmail());
            infoUploader(achievementInfo);
        }
    }
    private void photoUploader2() {

//        loaderLayout.setVisibility(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        title = tv_title.getText().toString();

        AchievementInfo achievementInfo = new AchievementInfo(mode + level + title, String.valueOf(selected_idx), et1.getText().toString(), et2.getText().toString(), et3.getText().toString(), et4.getText().toString(), profilePath, user.getEmail());
        infoUploader(achievementInfo);

    }
    private void infoDelete(String title) {
        //Date date = userList.size() == 0 || clear ? new Date() : userList.get(userList.size() - 1).getCreatedAt();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(user.getUid()).document(mode+level+title);
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);

                    }
                });
    }
    private void infoUploader(AchievementInfo achievementInfo) {
        HashMap<String,String>hashMap_tmp = new HashMap<>();
        hashMap_tmp.put("photoUrl",achievementInfo.getPhotoUrl());
        hashMap_tmp.put("achievement",achievementInfo.getAchievement());
        hashMap_tmp.put("user_id",achievementInfo.getUser_id());
        hashMap_tmp.put("n_Miss",achievementInfo.getN_Miss());
        hashMap_tmp.put("n_Great",achievementInfo.getN_Great());
        hashMap_tmp.put("songInfo",mode+level+title);
        hashMap_tmp.put("n_Bad",achievementInfo.getN_Bad());
        hashMap_tmp.put("n_Good",achievementInfo.getN_Good());
        userData.put(mode+level+title,hashMap_tmp);
        userUpdate = true;
        userLevelList.clear();
        levelInfoAdapter.setRank(selected_idx);
        Log.d(TAG, "clear");
        loaderLayout2.setVisibility(View.GONE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5) {
//            String profilePath_tmp = "file://"+setImageAndSaveImageReturnPath(data);
            profilePath =  setImageAndSaveImageReturnPath(data);
//            profilePath = data.getDataString();
            showToast(LevelInfoActivity.this,profilePath );
            Glide.with(this).load(data.getDataString()).centerInside().override(500).into(iv_profile);
//            showToast(LevelInfoActivity.this,profilePath_tmp);
            Log.d("Picture Path", profilePath);
        }
        if(requestCode== TAKE_FROM_GALLERY){
            if(resultCode==RESULT_OK) {
                profilePath = getRealPathFromURI(data.getData());
                Glide.with(this).load(data.getDataString()).centerInside().override(500).into(iv_profile);
            }else{
                profilePath="";
            }
        }

    }

    public String setImageAndSaveImageReturnPath(Intent data) {

        try {
            // URI 가져오기
            Uri selectedImageUri = data.getData();

            // 선택한 이미지에서 비트맵 생성
            InputStream in = getContentResolver().openInputStream(selectedImageUri);
            Bitmap img = BitmapFactory.decodeStream(in);
            in.close();

            String path = getString(R.string.app_name);
            String fileName = "/" + mode+level+title + ".png";
            String externalPath = Environment.getExternalStorageDirectory().getAbsolutePath()+'/'+path;
//            String path = getString(R.string.app_name);
//            String fileName = "/" + System.currentTimeMillis() + ".png";
//            String externalPath = getExternalPath(path);
            String address = externalPath + fileName;

//            String fileStr = "file://"+address;
//            Uri uri = getImageContentUri(fileStr);
//            showToast(LevelInfoActivity.this, uri.toString());



            //imagePath1 = address;
            //Toast.makeText(context, "imagePath1", Toast.LENGTH_SHORT).show();

            BufferedOutputStream out = null;

            File dirFile = new File(externalPath);

            if (!dirFile.isDirectory()) {
                dirFile.mkdirs();
            }

            File copyFile = new File(address);

            try {
                copyFile.createNewFile();
                out = new BufferedOutputStream(new FileOutputStream(copyFile));
                img.compress(Bitmap.CompressFormat.PNG, 100, out);
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                        Uri.fromFile(copyFile)));
                Log.d(TAG, "이미지저장됨");
                //Toast.makeText(getActivity(), captureMessage, Toast.LENGTH_LONG).show();
                // 저장되었다는 문구 생성
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "에러");
            }

            return address;

            // 비트맵 따로 경로에 저장하고
            // 그거 파일 패스 가져와야할듯

        } catch (Exception e) {
            e.printStackTrace();
            return "null";
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
            if (size < 0) {
                size = 0;
            }
            int height = 0;
            Paint paint = new Paint();
            int iWidth = 0;
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            int constHeight = 0;
            if (adapter.getItemCount() == 0) {
                showToast(LevelInfoActivity.this, "저장할 사진이 없습니다!");
                return;
            }
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
//                File dir = new File(
//                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "PIU_CAPTURE");
//                if(!dir.exists()){
//                    dir.mkdir();
//                    if(!dir.exists()) {
//                        showToast(LevelInfoActivity.this, "폴더생성 실패");
//                    }
//                }

                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),"/PIU_CAPTURE");
                if(!dir.exists()){
                    if(!dir.mkdirs()) {
                        showToast(LevelInfoActivity.this, "폴더생성 실패");
                    }
                }

                long now = System.currentTimeMillis();
                // 현재시간을 date 변수에 저장한다.
                Date date = new Date(now);
                // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
                SimpleDateFormat sdfNow = new SimpleDateFormat("HH-mm-ss");
                // nowDate 변수에 값을 저장한다.
                String formatDate = sdfNow.format(date);

                String string_path = dir.getAbsolutePath() + "/" + mode + level + formatDate + ".png";
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();

                OutputStream fOut = null;
                File file = new File(path, mode + level + formatDate + ".png"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                try{
                    fOut = new FileOutputStream(file);
                    Bitmap pictureBitmap = bigBitmap;// obtaining the Bitmap
                    pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                    fOut.flush(); // Not really required
                    fOut.close(); // do not forget to close the stream
                    MediaStore.Images.Media.insertImage(getContentResolver(),bigBitmap,mode + level + formatDate + ".png",null);
                    showToast(LevelInfoActivity.this, "capture success");
                }catch (FileNotFoundException exception) {
                    showToast(LevelInfoActivity.this, "die1.");
                    Log.e("FileNotFoundException", exception.getMessage());
                } catch (IOException exception) {
                    Log.e("IOException", exception.getMessage());
                    showToast(LevelInfoActivity.this, "die2.");
                }




//                try {
//                    FileOutputStream out = new FileOutputStream(string_path);
//                    bigBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//                    showToast(LevelInfoActivity.this, "capture success");
//                    out.close();
//                } catch (FileNotFoundException exception) {
//                    showToast(LevelInfoActivity.this, "die1.");
//                    Log.e("FileNotFoundException", exception.getMessage());
//                } catch (IOException exception) {
//                    Log.e("IOException", exception.getMessage());
//                    showToast(LevelInfoActivity.this, "die2.");
//                }

            }
        }
    }



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
            String dd;
            if(mode.equals("Coop"))
            {
                dd = (String) detailDifficulty_tmp.get(mode + '_'+level);
            }else{
                dd = (String) detailDifficulty_tmp.get(mode + level);
            }

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
        for (int idx = 0; idx < song_name.length; idx++) {
            if(userData.get(song_name[idx])!=null){
                userLevelList.put(song_name[idx],((HashMap<String,String>)userData.get(song_name[idx])).get("achievement"));
            }

        }
        if(init) {
            postsUpdate(false, -1);
        }else{
            songInfoUpdate();
        }


//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection(user.getUid())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            boolean isHere = false;
//                            for (QueryDocumentSnapshot document : task.getResult()) {
////                                String cur = document.getData().get("songInfo").toString();
////                                if (cur.substring(0, 3).equals(mode + level)) {
////                                    isHere = true;
////                                    userLevelList.put(cur, document.getData().get("achievement").toString());
////                                } else {
////                                    if(isHere){
////                                        break;
////                                    }
////                                }
//
//                                Log.d(TAG, "testcnt : " + String.valueOf(testcnt) + document.getId() + " => " + document.getData()+"size : "+String.valueOf(userLevelList.size()));
//                            }
//                            if(init) {
//                                postsUpdate(false, -1);
//                            }else{
//                                songInfoUpdate();
//                            }
//                        } else {
//                            showToast(LevelInfoActivity.this, user.getUid().toString() + "reading fail");
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
////                            levelInfoAdapter.notifyDataSetChanged();
//                    }
//                });
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
//        intent.putExtra("setCategory",category);
        intent.putExtra("setLevel", newLevel);
        intent.putExtra("setMode", mode);
        startActivityForResult(intent, 1);
    }
}