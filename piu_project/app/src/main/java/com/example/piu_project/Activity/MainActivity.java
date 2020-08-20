package com.example.piu_project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.piu_project.ListItem;
import com.example.piu_project.R;
import com.example.piu_project.adapter.ListViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.example.piu_project.Util.showToast;

public class MainActivity extends BasicActivity {
    private static final String TAG = "MainActivity";
    private RelativeLayout settingBackgroundLayout1;
    private RelativeLayout settingBackgroundLayout2;
    private RelativeLayout loaderLayout;
    private int page = 1;
    private FirebaseUser user;
    private String level="";
    private String mode="";
    private String category="";
    public static HashMap<String,Object> allData = new HashMap<>();
    public static HashMap<String,Object> userData = new HashMap<>();
    private ListView listview1;
    private ListView listview2;
    private ListView listview3;
    private ListViewAdapter listViewAdapter1;
    private ListViewAdapter listViewAdapter2;
    private ListViewAdapter listViewAdapter3;
    private ArrayList<ListItem> itemList1 = new ArrayList<ListItem>() ;
    private ArrayList<ListItem> itemList2 = new ArrayList<ListItem>() ;
    private ArrayList<ListItem> itemList3 = new ArrayList<ListItem>() ;
//    private Fragment curFragment;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbarTitle("piu project");
        loaderLayout = findViewById(R.id.loaderLyaout);
        loaderLayout.bringToFront();


        init();

    }


    private void init(){

        listViewAdapter1 = new ListViewAdapter(itemList1) ;
        listViewAdapter2 = new ListViewAdapter(itemList2) ;
        listViewAdapter3 = new ListViewAdapter(itemList3) ;
        // 리스트뷰 참조 및 Adapter달기
        listview1 = (ListView) findViewById(R.id.listview1);
        listview2 = (ListView) findViewById(R.id.listview2);
        listview3 = (ListView) findViewById(R.id.listview3);
        listSetup();
        listview1.setAdapter(listViewAdapter1);
        listview2.setAdapter(listViewAdapter2);
        listview3.setAdapter(listViewAdapter3);
        settingBackgroundLayout1 =findViewById(R.id.settingBackgroundLayout1);
        settingBackgroundLayout1.setOnClickListener(onClickListener);
        settingBackgroundLayout2 =findViewById(R.id.settingBackgroundLayout2);
        settingBackgroundLayout2.setOnClickListener(onClickListener);
        findViewById(R.id.bt_level).setOnClickListener(onClickListener);
        findViewById(R.id.bt_category).setOnClickListener(onClickListener);
        findViewById(R.id.bt_myPage).setOnClickListener(onClickListener);
        findViewById(R.id.bt_logout).setOnClickListener(onClickListener);
        findViewById(R.id.bt_set_level).setOnClickListener(onClickListener);
        findViewById(R.id.bt_set_category).setOnClickListener(onClickListener);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            myStartActivity(LoginActivity.class);
//        } else {
//            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid());
//            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document != null) {
//                            if (document.exists()) {
//                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                            } else {
//                                Log.d(TAG, "No such document");
//                                myStartActivity(MemberInitActivity.class);
//                            }
//                        }
//                    } else {
//                        Log.d(TAG, "get failed with ", task.getException());
//                    }
//                }
//            });
//            HomeFragment homeFragment = new HomeFragment();
//            curFragment = homeFragment;
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, homeFragment)
//                    .commit();

//            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
//            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//                @Override
//                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                    switch (item.getItemId()) {
//                        case R.id.home:
//                            if(curFragment != null){
//                                getSupportFragmentManager().beginTransaction().remove(curFragment).commit();
//                                curFragment = null;
//                            }
//                            if(settingBackgroundLayout.getVisibility()==View.VISIBLE){
//                                settingBackgroundLayout.setVisibility(View.GONE);
//                            }
//                            HomeFragment homeFragment = new HomeFragment();
//                            curFragment = homeFragment;
//                            getSupportFragmentManager().beginTransaction()
//                                    .replace(R.id.container, homeFragment)
//                                    .commit();
//                            return true;
//                        case R.id.myInfo:
//                            if(curFragment != null){
//                                getSupportFragmentManager().beginTransaction().remove(curFragment).commit();
//                                curFragment = null;
//                            }
//                            if(settingBackgroundLayout.getVisibility()==View.VISIBLE){
//                                settingBackgroundLayout.setVisibility(View.GONE);
//                            }
//
//                            UserInfoFragment userInfoFragment = new UserInfoFragment();
//                            curFragment = userInfoFragment;
//                            getSupportFragmentManager().beginTransaction()
//                                    .replace(R.id.container, userInfoFragment)
//                                    .commit();
//                            return true;
//                        case R.id.userList:
//                            if(settingBackgroundLayout.getVisibility()==View.VISIBLE){
//                                settingBackgroundLayout.setVisibility(View.GONE);
//                            }
//                            return true;
//                        case R.id.bt_level:
//                            if(curFragment != null){
//                                getSupportFragmentManager().beginTransaction().remove(curFragment).commit();
//                                curFragment = null;
//                            }
//                            settingBackgroundLayout.setVisibility(View.VISIBLE);
//
//                            Log.d("setvisibility","Success");
//                            return true;
//                        case R.id.category:
//                            if(curFragment != null){
//                                getSupportFragmentManager().beginTransaction().remove(curFragment).commit();
//                                curFragment = null;
//                            }
//                            settingBackgroundLayout.setVisibility(View.VISIBLE);
//                            Log.d("setvisibility","Success");
//                            return true;
//                    }
//                    return false;
//                }
//            });
        }
    }

    private void listSetup(){
//        String[] info = getResources().getStringArray(R.array.name_s07);
//        for(int i=0;i<info.length;i++){
//            Log.d(TAG,info[i]);
//        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.d(TAG, document.getId() + " => " + document.getData());
                                userData.put((String)document.getData().get("songInfo"),(Object) document.getData());
                            }
                            loaderLayout.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        FirebaseDatabase database = FirebaseDatabase.getInstance();                      //Firebase database와 연동;
        DatabaseReference databaseReference_s = database.getReference("db_song");     //DB 테이블 연결
        databaseReference_s.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allData = (HashMap<String,Object>)dataSnapshot.getValue();
            }
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //error 발생시
                Log.e(TAG, String.valueOf(databaseError.toException()));
            }
        });

        String[] mode_string = getResources().getStringArray(R.array.mode_string);
        for(int i=0;i<mode_string.length;i++){
            listViewAdapter1.addItem(mode_string[i]);
        }
        String[] level_string = getResources().getStringArray(R.array.level_string);
        for(int i=0;i<level_string.length;i++){
            listViewAdapter2.addItem(level_string[i]);
        }
        String[] category_string = getResources().getStringArray(R.array.category_string);
        for(int i=0;i<category_string.length;i++){
            listViewAdapter3.addItem(category_string[i]);
        }

        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToggleButton tb = (ToggleButton)((LinearLayout)view).getChildAt(0);
                if(tb.isChecked()){
                    mode="";
                }else {
                    mode = tb.getText().toString();
                    tb.setChecked(true);
                }
            }
        });
        listview1.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                for(int i = 0;i<visibleItemCount;i++){
                    if(firstVisibleItem+i<totalItemCount){
                        ToggleButton tb = ((ToggleButton) ((LinearLayout) view.getChildAt(i)).getChildAt(0));
                        if(tb.getText().toString().equals(mode)) {
                            tb.setChecked(true);
                        }else{
                            tb.setChecked(false);
                        }
                    }
                }
            }
        });
        listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToggleButton tb = (ToggleButton)((LinearLayout)view).getChildAt(0);
                if(tb.isChecked()){
                    level="";
                }else {
                    level = tb.getText().toString();
                    tb.setChecked(true);
                }
            }
        });
        listview2.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                for(int i = 0;i<visibleItemCount;i++){
                    if(firstVisibleItem+i<totalItemCount){
                        ToggleButton tb = ((ToggleButton) ((LinearLayout) view.getChildAt(i)).getChildAt(0));
                        if(tb.getText().toString().equals(level)) {
                            tb.setChecked(true);
                        }else{
                            tb.setChecked(false);
                        }
                    }
                }
            }
        });
        listview3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToggleButton tb = (ToggleButton)((LinearLayout)view).getChildAt(0);
                if(tb.isChecked()){
                    category="";
                    tb.setChecked(false);
                }else {
                    for(int i=0;i<3;i++) {
                        ((ToggleButton)((LinearLayout)listview3.getChildAt(i)).getChildAt(0)).setChecked(false);
                    }
                    category = tb.getText().toString();
                    tb.setChecked(true);
                }
            }
        });
        listview3.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                for(int i = 0;i<visibleItemCount;i++){
                    if(firstVisibleItem+i<totalItemCount){
                        ToggleButton tb = ((ToggleButton) ((LinearLayout) view.getChildAt(i)).getChildAt(0));
                        if(tb.getText().toString().equals(category)) {
                            tb.setChecked(true);
                        }else{
                            tb.setChecked(false);
                        }
                    }
                }
            }
        });
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_level:
                    settingBackgroundLayout1.setVisibility(View.VISIBLE);
                    page = 1;
                    break;
                case R.id.bt_category:
                    myStartActivity(CategoryInfoActivity.class);
//                    settingBackgroundLayout2.setVisibility(View.VISIBLE);
//                    page = 2;
                    break;
                case R.id.bt_myPage:
                    myStartActivity(MyPageActivity.class);
                    break;
                case R.id.bt_logout:
                    FirebaseAuth.getInstance().signOut();
                    myStartActivity(LoginActivity.class);
                    break;
                case R.id.settingBackgroundLayout1:
                    settingBackgroundLayout1.setVisibility(View.GONE);
                    break;
                case R.id.settingBackgroundLayout2:
                    settingBackgroundLayout2.setVisibility(View.GONE);
                    break;
                case R.id.bt_set_level:
                            if(mode.equals("")||level.equals("")){
                                showToast(MainActivity.this, "정보를 다 입력해주세요!");
                            } else {
                                settingBackgroundLayout1.setVisibility(View.GONE);
                                myStartActivity(LevelInfoActivity.class);
                                mode = "";
                                level = "";

                        }
                            break;

                case R.id.bt_set_category:

//                            if(category.equals("")){
//                                showToast(MainActivity.this, "정보를 다 입력해주세요!");
//                            } else {
//                                settingBackgroundLayout2.setVisibility(View.GONE);
//
//                                category="";
//                            }
//                            break;
//                    LevelInfoFragment levelInfoFragment = new LevelInfoFragment();
//                    levelInfoFragment.SetLevel(setLevel);
//                    curFragment = levelInfoFragment;
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.container, levelInfoFragment)
//                            .commit();
                    break;
            }
        }
    };
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        if(mode.equals("Single")){
            mode = "S";
        } else if (mode.equals("Double")){
            mode="D";
        }
        else if(mode.equals("SinglePerformance")){
            mode = "SP";
        }
        else if(mode.equals("DoublePerformance")){
            mode = "DP";
        }
        else{
            mode = "C";
        }
        intent.putExtra("setCategory",category);
        intent.putExtra("setLevel", level);
        intent.putExtra("setMode", mode);
        startActivityForResult(intent, 1);
    }
    @Override
    public void onBackPressed() {
        if(settingBackgroundLayout1.getVisibility()==View.VISIBLE) {
            settingBackgroundLayout1.setVisibility(View.GONE);
        }else if(settingBackgroundLayout2.getVisibility()==View.VISIBLE) {
            settingBackgroundLayout2.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }
}
