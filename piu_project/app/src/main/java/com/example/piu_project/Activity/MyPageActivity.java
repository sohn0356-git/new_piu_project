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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import static com.example.piu_project.Util.showToast;
public class MyPageActivity extends BasicActivity {
    private RelativeLayout loaderLayout;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private HashMap<String,Object> userData =  new HashMap<>();
    private int cnt_ts=0;
    private int cnt_ds=0;
    private int cnt_ss=0;
    private TextView tv_ts;
    private TextView tv_ds;
    private TextView tv_ss;
    private TextView tv_email;
    private static final String TAG = "MyPageActivity";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        setToolbarTitle("내정보");
        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        String email = user.getEmail();

        db = FirebaseFirestore.getInstance();

        Log.d(TAG,userData.toString());
        loaderLayout = findViewById(R.id.loaderLyaout);
        loaderLayout.bringToFront();
        tv_email = (TextView)findViewById(R.id.tv_email);
        tv_ts = (TextView)findViewById(R.id.tv_ts);
        tv_ds = (TextView)findViewById(R.id.tv_ds);
        tv_ss = (TextView)findViewById(R.id.tv_ss);
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
        userUpdate();

        tv_email.setText("이메일 : "+email);


        Log.d(TAG,userData.entrySet().toString());
//        findViewById(R.id.bt_level).setOnClickListener(onClickListener);

    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void userUpdate(){
        userData =  new HashMap<>();
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
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
