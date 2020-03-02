package com.example.piu_project.Activity;

import android.content.Intent;
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

import androidx.annotation.NonNull;

import com.example.piu_project.ListItem;
import com.example.piu_project.R;
import com.example.piu_project.adapter.ListViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends BasicActivity {
    private static final String TAG = "MainActivity";
    private RelativeLayout settingBackgroundLayout;
    private int page = 1;
    private String level;
    private String mode;
    ListView listview1;
    ListView listview2;
    ListViewAdapter listViewAdapter1;
    ListViewAdapter listViewAdapter2;
    private ArrayList<ListItem> itemList1 = new ArrayList<ListItem>() ;
    private ArrayList<ListItem> itemList2 = new ArrayList<ListItem>() ;
//    private Fragment curFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbarTitle(getResources().getString(R.string.app_name));
        init();

    }


    private void init(){

        listViewAdapter1 = new ListViewAdapter(itemList1) ;
        listViewAdapter2 = new ListViewAdapter(itemList2) ;
        // 리스트뷰 참조 및 Adapter달기
        listview1 = (ListView) findViewById(R.id.listview1);
        listview2 = (ListView) findViewById(R.id.listview2);
        listview1.setAdapter(listViewAdapter1);
        listview2.setAdapter(listViewAdapter2);

        listSetup();
        settingBackgroundLayout =findViewById(R.id.settingBackgroundLayout);
        settingBackgroundLayout.setOnClickListener(onClickListener);
        findViewById(R.id.bt_level).setOnClickListener(onClickListener);
        findViewById(R.id.bt_category).setOnClickListener(onClickListener);
        findViewById(R.id.bt_logout).setOnClickListener(onClickListener);
        findViewById(R.id.bt_set_level).setOnClickListener(onClickListener);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            myStartActivity(LoginActivity.class);
        } else {
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                                myStartActivity(MemberInitActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
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
        listViewAdapter1.addItem("Single");
        listViewAdapter1.addItem("Double");
        listViewAdapter1.addItem("Coop");
        listViewAdapter2.addItem("01");
        listViewAdapter2.addItem("02");
        listViewAdapter2.addItem("03");
        listViewAdapter2.addItem("04");
        listViewAdapter2.addItem("05");
        listViewAdapter2.addItem("06");
        listViewAdapter2.addItem("07");
        listViewAdapter2.addItem("08");
        listViewAdapter2.addItem("09");
        listViewAdapter2.addItem("10");
        listViewAdapter2.addItem("11");
        listViewAdapter2.addItem("12");
        listViewAdapter2.addItem("13");
        listViewAdapter2.addItem("14");
        listViewAdapter2.addItem("15");
        listViewAdapter2.addItem("16");
        listViewAdapter2.addItem("17");
        listViewAdapter2.addItem("18");
        listViewAdapter2.addItem("19");
        listViewAdapter2.addItem("20");
        listViewAdapter2.addItem("21");
        listViewAdapter2.addItem("22");
        listViewAdapter2.addItem("23");
        listViewAdapter2.addItem("24");
        listViewAdapter2.addItem("25");
        listViewAdapter2.addItem("26");
        listViewAdapter2.addItem("27");
        listViewAdapter2.addItem("28");
        listViewAdapter2.addItem("29");
        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToggleButton tb = (ToggleButton)((LinearLayout)view).getChildAt(0);
                if(tb.isChecked()){
                    mode="";
                }else {
                    mode = tb.getText().toString();
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
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_level:
                    settingBackgroundLayout.setVisibility(View.VISIBLE);
                    page = 1;
                    break;
                case R.id.bt_category:
                    settingBackgroundLayout.setVisibility(View.VISIBLE);
                    page = 2;
                    break;
                case R.id.bt_logout:
                    FirebaseAuth.getInstance().signOut();
                    myStartActivity(LoginActivity.class);
                    break;
                case R.id.settingBackgroundLayout:
                    settingBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.bt_set_level:
                    settingBackgroundLayout.setVisibility(View.GONE);
                    if(page==1) {
                        myStartActivity(LevelInfoActivity.class);
                    }else{

                    }
                    break;
//                    LevelInfoFragment levelInfoFragment = new LevelInfoFragment();
//                    levelInfoFragment.SetLevel(setLevel);
//                    curFragment = levelInfoFragment;
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.container, levelInfoFragment)
//                            .commit();
//                    break;
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
        else{
            mode = "C";
        }
        intent.putExtra("setLevel", level);
        intent.putExtra("setMode", mode);
        startActivityForResult(intent, 1);
    }
}
