package com.example.base_piu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Auth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            myStartActivity(SignUpActivity.class);
        }
        else {
            for (UserInfo profile : user.getProviderData()) {
                String name = profile.getDisplayName();
                if (name != null) {
                    if (name.length() == 0) {
                        myStartActivity(MemberActivity.class);
                    }
                }
            }
        }
        findViewById(R.id.bt_logout).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_logout:
                    FirebaseAuth.getInstance().signOut();
                    myStartActivity(SignUpActivity.class);
                    break;
            }
        }
    };
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 1);
    }
}
