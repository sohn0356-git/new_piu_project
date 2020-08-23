package com.example.piu_project.Activity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.piu_project.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;



public class MyAchievementActivity extends BasicActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "MyAchievementActivity";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_achievement);
        setToolbarTitle("내정보");


    }


}
