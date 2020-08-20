package com.example.piu_project.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

        int cnt_ts=0;
        int cnt_ds=0;
        int cnt_ss=0;
        TextView tv_ts = (TextView)findViewById(R.id.tv_ts);
        TextView tv_ds = (TextView)findViewById(R.id.tv_ds);
        TextView tv_ss = (TextView)findViewById(R.id.tv_ss);

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
