package com.example.base_piu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MemberActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "MemberActivity";
    EditText et_name;
    EditText et_phoneNumber;
    EditText et_birthday;
    EditText et_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        et_name = (EditText)findViewById(R.id.et_name);
        et_phoneNumber = (EditText)findViewById(R.id.et_phoneNumber);
        et_birthday = (EditText)findViewById(R.id.et_birthday);
        et_address = (EditText)findViewById(R.id.et_address);
        findViewById(R.id.bt_check).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_check:
                    profileUpdate();
                    break;
            }
        }
    };
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
   }

    private void profileUpdate() {
        String name = et_name.getText().toString();
        String phoneNumber = et_phoneNumber.getText().toString();
        String birthday = et_birthday.getText().toString();
        String address = et_address.getText().toString();
        if (name.length() > 0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();
            if(user!=null) {
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated.");
                                    startToast("회원정보 등록을 성공했습니다!");
                                    finish();
                                }
                            }
                        });
            }
        } else{
            startToast("모든 회원정보를 입력해주세요");
        }
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
