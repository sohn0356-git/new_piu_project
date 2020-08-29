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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.piu_project.Activity.MainActivity.userData;
import static com.example.piu_project.Util.showToast;
public class MyPageActivity extends BasicActivity {
    private RelativeLayout loaderLayout;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private int cnt_ts=0;
    private int cnt_ds=0;
    private int cnt_ss=0;
    private TextView tv_ts;
    private TextView tv_ds;
    private TextView tv_ss;
    private TextView tv_email;
    private String email;
    private static final String TAG = "MyPageActivity";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        setToolbarTitle("내정보");
        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        email = user.getEmail();
        db = FirebaseFirestore.getInstance();

        Log.d(TAG,userData.toString());
        loaderLayout = findViewById(R.id.loaderLyaout);
        loaderLayout.bringToFront();
        tv_email = (TextView)findViewById(R.id.tv_email);
        tv_ts = (TextView)findViewById(R.id.tv_ts);
        tv_ds = (TextView)findViewById(R.id.tv_ds);
        tv_ss = (TextView)findViewById(R.id.tv_ss);
        findViewById(R.id.bt_pwChange).setOnClickListener(onClickListener);
        findViewById(R.id.bt_logout).setOnClickListener(onClickListener);
        findViewById(R.id.bt_out).setOnClickListener(onClickListener);
        userUpdate();

        tv_email.setText("이메일 : "+email);


        Log.d(TAG,userData.entrySet().toString());
//        findViewById(R.id.bt_level).setOnClickListener(onClickListener);

    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_logout:
                    FirebaseAuth.getInstance().signOut();
                    myStartActivity(LoginActivity.class);
                    break;
                case R.id.bt_pwChange:
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        showToast(MyPageActivity.this,"패스워드 변경 메일을 발송하였습니다.");
                                    }
                                }
                            });
                    break;
                case R.id.bt_out:
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
                    SimpleDateFormat sdfTitle = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH-mm-ss");
                    // nowDate 변수에 값을 저장한다.
                    String formatDate = sdfNow.format(date);
                    Map<String, Object> time = new HashMap<>();
                    time.put("시간", formatDate);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(user.getUid()).document(sdfTitle.format(date)+"탈퇴").set(time)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    showToast(MyPageActivity.this, "계정을 삭제하였습니다.");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseAuth.getInstance().signOut();
                                        Log.d(TAG, "User account deleted.");
                                        myStartActivity(LoginActivity.class);
                                        finish();
                                    }
                                }
                            });
                    break;
            }
        }
    };

    private void userUpdate(){
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
//        userData =  new HashMap<>();
//        db.collection(user.getUid())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                userData.put((String)document.getData().get("songInfo"),(Object) document.getData());
//                            }
//                            loaderLayout.setVisibility(View.GONE);
//                            for ( HashMap.Entry<String, Object> entry : userData.entrySet() ) {
//                                String a = ((HashMap<String,String>)entry.getValue()).get("achievement");
//                                if(a.equals("0")){
//                                    cnt_ts++;
//                                }else if(a.equals("1")){
//                                    cnt_ds++;
//                                }else if(a.equals("2")){
//                                    cnt_ss++;
//                                }
//                            }
//                            tv_ts.setText("Count :   "+cnt_ts);
//                            tv_ds.setText("Count :   "+cnt_ds);
//                            tv_ss.setText("Count :   "+cnt_ss);
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
    }
}
