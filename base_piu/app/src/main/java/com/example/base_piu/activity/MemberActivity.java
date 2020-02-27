package com.example.base_piu.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.base_piu.MemberInfo;
import com.example.base_piu.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MemberActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "MemberActivity";
    EditText et_name;
    EditText et_phoneNumber;
    EditText et_birthday;
    EditText et_address;
    private FirebaseUser user;
    private ImageView profileImageView;
    private String profilePath;
    private RelativeLayout buttonBackgroundLayout;
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
        buttonBackgroundLayout = (RelativeLayout)findViewById(R.id.buttonsBackgroundLayout);
        findViewById(R.id.bt_check).setOnClickListener(onClickListener);
        findViewById(R.id.bt_gallery).setOnClickListener(onClickListener);
        findViewById(R.id.bt_picture).setOnClickListener(onClickListener);
        profileImageView = (ImageView)findViewById(R.id.profileImageView);
        profileImageView.setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_check:
                    storageUploader();
                    break;
                case R.id.cv_image:
                    myStartActivity(CameraActivity.class);
                    break;
                case R.id.profileImageView:
                    buttonBackgroundLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.buttonsBackgroundLayout:
                    buttonBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.bt_gallery:
                    // Here, thisActivity is the current activity
                    if (ContextCompat.checkSelfPermission(MemberActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MemberActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MemberActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        } else {
                        }
                    } else {
                        myStartActivity(GalleryActivity.class);
                    }

                    break;
            }
        }
    };
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myStartActivity(GalleryActivity.class);
                } else {
                    startToast("권한을 허용해주세요");
                }
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
   }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if(resultCode== Activity.RESULT_OK){
                    profilePath = data.getStringExtra("path");
                    Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                    profileImageView.setImageBitmap(bmp);
                    buttonBackgroundLayout.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void storageUploader() {
        final String name = ((EditText) findViewById(R.id.et_name)).getText().toString();
        final String phoneNumber = ((EditText) findViewById(R.id.et_phoneNumber)).getText().toString();
        final String birthDay = ((EditText) findViewById(R.id.et_birthday)).getText().toString();
        final String address = ((EditText) findViewById(R.id.et_address)).getText().toString();

        if (name.length() > 0 && phoneNumber.length() > 9 && birthDay.length() > 5 && address.length() > 0) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");

            if (profilePath == null) {
                MemberInfo memberInfo = new MemberInfo(name, phoneNumber, birthDay, address);
                storeUploader(memberInfo);
            } else {
                try {
                    InputStream stream = new FileInputStream(new File(profilePath));
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                MemberInfo userInfo = new MemberInfo(name, phoneNumber, birthDay, address, downloadUri.toString());
                                storeUploader(userInfo);
                            } else {
                                startToast("회원정보를 보내는데 실패하였습니다.");
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.e("로그", "에러: " + e.toString());
                }
            }
        } else {
            startToast("회원정보를 입력해주세요.");
        }
    }

    private void storeUploader(MemberInfo memberInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startToast("회원정보 등록을 성공하였습니다.");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startToast("회원정보 등록에 실패하였습니다.");
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}
