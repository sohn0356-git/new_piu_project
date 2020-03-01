package com.example.piu_project.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.piu_project.R;
import com.example.piu_project.fragment.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.piu_project.fragment.UserInfoFragment;
import com.example.piu_project.fragment.LevelInfoFragment;

public class MainActivity extends BasicActivity {
    private static final String TAG = "MainActivity";
    private RelativeLayout settingBackgroundLayout;
    private EditText et_set_level;
    private int setLevel = 1;
    private Fragment curFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbarTitle(getResources().getString(R.string.app_name));
        init();

    }
    private void init(){

        et_set_level = (EditText)findViewById(R.id.et_set_level);
        settingBackgroundLayout =findViewById(R.id.settingBackgroundLayout);

        findViewById(R.id.bt_set_level).setOnClickListener(onClickListener);
        settingBackgroundLayout.setOnClickListener(onClickListener);
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
            HomeFragment homeFragment = new HomeFragment();
            curFragment = homeFragment;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, homeFragment)
                    .commit();

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.home:
                            if(curFragment != null){
                                getSupportFragmentManager().beginTransaction().remove(curFragment).commit();
                                curFragment = null;
                            }
                            if(settingBackgroundLayout.getVisibility()==View.VISIBLE){
                                settingBackgroundLayout.setVisibility(View.GONE);
                            }
                            HomeFragment homeFragment = new HomeFragment();
                            curFragment = homeFragment;
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, homeFragment)
                                    .commit();
                            return true;
                        case R.id.myInfo:
                            if(curFragment != null){
                                getSupportFragmentManager().beginTransaction().remove(curFragment).commit();
                                curFragment = null;
                            }
                            if(settingBackgroundLayout.getVisibility()==View.VISIBLE){
                                settingBackgroundLayout.setVisibility(View.GONE);
                            }

                            UserInfoFragment userInfoFragment = new UserInfoFragment();
                            curFragment = userInfoFragment;
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, userInfoFragment)
                                    .commit();
                            return true;
                        case R.id.userList:
                            if(settingBackgroundLayout.getVisibility()==View.VISIBLE){
                                settingBackgroundLayout.setVisibility(View.GONE);
                            }
                            return true;
                        case R.id.level:
                            if(curFragment != null){
                                getSupportFragmentManager().beginTransaction().remove(curFragment).commit();
                                curFragment = null;
                            }
                            settingBackgroundLayout.setVisibility(View.VISIBLE);

                            Log.d("setvisibility","Success");
                            return true;
                        case R.id.category:
                            if(curFragment != null){
                                getSupportFragmentManager().beginTransaction().remove(curFragment).commit();
                                curFragment = null;
                            }
                            settingBackgroundLayout.setVisibility(View.VISIBLE);
                            Log.d("setvisibility","Success");
                            return true;
                    }
                    return false;
                }
            });
        }
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.settingBackgroundLayout:
                    settingBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.bt_set_level:
                    setLevel = Integer.parseInt(et_set_level.getText().toString());
                    Log.d("Level"," is "+String.valueOf(setLevel));
                    settingBackgroundLayout.setVisibility(View.GONE);
                    LevelInfoFragment levelInfoFragment = new LevelInfoFragment();
                    levelInfoFragment.SetLevel(setLevel);
                    curFragment = levelInfoFragment;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, levelInfoFragment)
                            .commit();
                    break;
            }
        }
    };
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 1);
    }
}
