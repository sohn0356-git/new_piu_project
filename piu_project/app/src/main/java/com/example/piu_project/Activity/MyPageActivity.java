package com.example.piu_project.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.piu_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

import static com.example.piu_project.Util.showToast;
import static com.example.piu_project.Activity.MainActivity.userData;
public class MyPageActivity extends BasicActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "MyPageActivity";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        setToolbarTitle("내정보");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        int cnt_ts=0;
        int cnt_ds=0;
        int cnt_ss=0;
        Log.d(TAG,userData.toString());
        TextView tv_email = (TextView)findViewById(R.id.tv_email);
        TextView tv_ts = (TextView)findViewById(R.id.tv_ts);
        TextView tv_ds = (TextView)findViewById(R.id.tv_ds);
        TextView tv_ss = (TextView)findViewById(R.id.tv_ss);
        Button bt_pwChange = (Button)findViewById(R.id.bt_pwChange);
        bt_pwChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");
                                }
                            }
                        });
            }
        });

        Button bt_out = (Button)findViewById(R.id.bt_out);
        bt_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User account deleted.");
                                }
                            }
                        });
            }
        });

        tv_email.setText("이메일 : "+email);

        for ( HashMap.Entry<String, Object> entry : userData.entrySet() ) {
           String a = ((HashMap<String,String>)entry.getValue()).get("achievement");
           if(a.equals("0")){
               cnt_ts++;
           }else if(a.equals("1")){
               cnt_ds++;
           }else if(a.equals("2")){
               cnt_ss++;
           }
        }
        tv_ts.setText("Count :   "+cnt_ts);
        tv_ds.setText("Count :   "+cnt_ds);
        tv_ss.setText("Count :   "+cnt_ss);
        Log.d(TAG,userData.entrySet().toString());
//        findViewById(R.id.bt_level).setOnClickListener(onClickListener);

    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
