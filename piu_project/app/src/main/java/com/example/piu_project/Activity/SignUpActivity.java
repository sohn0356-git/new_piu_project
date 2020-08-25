package com.example.piu_project.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.example.piu_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.List;

import static com.example.piu_project.Util.showToast;

public class SignUpActivity extends BasicActivity {
    private static final String TAG = "SignUpActivity" ;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setToolbarTitle("회원가입");

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);
    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.signUpButton:

                    signUp();
                    break;
            }
        }
    };
    private void signUp() {


        String email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
        String password = ((EditText)findViewById(R.id.passwordEditText)).getText().toString();
        String passwordCheck = ((EditText)findViewById(R.id.passwordCheckEditText)).getText().toString();

        if(email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0){
            if(password.equals(passwordCheck)){
                final RelativeLayout loaderLayout = findViewById(R.id.loaderLyaout);
                loaderLayout.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                loaderLayout.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    showToast(SignUpActivity.this, "회원가입에 성공하였습니다.\n이메일을 인증해주세요");
                                    user.sendEmailVerification();
                                    myStartActivity(LoginActivity.class);
                                } else {
                                    if(task.getException() != null){
                                        showToast(SignUpActivity.this, task.getException().toString());
                                    }
                                }
                            }
                        });
            }else{
                showToast(SignUpActivity.this, "비밀번호가 일치하지 않습니다.");
            }
        }else {
            showToast(SignUpActivity.this, "이메일 또는 비밀번호를 입력해 주세요.");
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
