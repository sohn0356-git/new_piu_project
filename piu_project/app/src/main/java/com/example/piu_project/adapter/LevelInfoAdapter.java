package com.example.piu_project.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.piu_project.AchievementInfo;
import com.example.piu_project.Activity.GalleryActivity;
import com.example.piu_project.Activity.LevelInfoActivity;
import com.example.piu_project.Activity.ShowInfoActivity;
import com.example.piu_project.R;
import com.example.piu_project.SongInfo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.piu_project.Util.INTENT_PATH;
import static com.example.piu_project.Util.showToast;

public class LevelInfoAdapter extends RecyclerView.Adapter<LevelInfoAdapter.MainViewHolder> {
    private static final String TAG = "LevelInfoActivity";
    private ArrayList<SongInfo> mDataset;
    private Activity activity;
    private EditText et1;
    private EditText et2;
    private EditText et3;
    private EditText et4;
    private String level;
    private String mode;
    private String title;
    private int target_rank;
    private ImageView iv_profile;
    private RelativeLayout loaderLayout;
    private FirebaseUser user;
    private FirebaseFirestore firebaseFirestore;
    private Spinner spinner_level;
    private RelativeLayout settingBackgroundLayout;
    private ImageView iv_rank;
    private int[] img_rank = {R.drawable.level_s,R.drawable.level_s,R.drawable.level_a,R.drawable.level_a,R.drawable.level_a,R.drawable.level_a,R.drawable.level_a,R.drawable.level_a};
    private String[] rank =  {"SSS", "SS", "S", "A (Break on)", "A (Break off)", "B (Break on)", "B (Break off)", "C (Break on)", "C (Break off)", "D(Break on)", "D (Break off)", "F or Game Over", "No Play"};


    static class MainViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        MainViewHolder(CardView v) {
            super(v);
            cardView = v;

        }

    }

    public LevelInfoAdapter(Activity activity, ArrayList<SongInfo> myDataset, String mode, String level) {
        this.mDataset = myDataset;
        this.activity = activity;
        this.mode = mode;
        this.level = level;

        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @NonNull
    @Override
    public LevelInfoAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_level_list, parent, false);
        final MainViewHolder mainViewHolder = new MainViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = ((TextView)(v.findViewById(R.id.nameTextView))).getText().toString();
                et1 = (EditText)(activity).findViewById(R.id.editText1);
                et2 = (EditText)(activity).findViewById(R.id.editText2);
                et3 = (EditText)(activity).findViewById(R.id.editText3);
                et4 = (EditText)(activity).findViewById(R.id.editText4);
                user = FirebaseAuth.getInstance().getCurrentUser();
                et1.setText("");
                et2.setText("");
                et3.setText("");
                et4.setText("");
                ((TextView)(activity).findViewById(R.id.tv_title)).setText(title);
                iv_profile = (ImageView)(activity.findViewById(R.id.iv_profile));
                settingBackgroundLayout = (activity.findViewById(R.id.settingBackgroundLayout));
                settingBackgroundLayout.setVisibility(View.VISIBLE);
                loaderLayout = activity.findViewById(R.id.loaderLyaout);
                loaderLayout.setVisibility(View.VISIBLE);
                settingBackgroundLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        settingBackgroundLayout.setVisibility(View.GONE);
                    }
                });
                iv_rank = (ImageView)cardView.findViewById(R.id.iv_rank);
                (activity.findViewById(R.id.settingBackgroundLayout)).bringToFront();
                (activity.findViewById(R.id.bt_gotoInfo)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myStartActivity(ShowInfoActivity.class, mDataset.get(mainViewHolder.getAdapterPosition()));
                    }
                });


                infoUpdate(title);
                findPicture(title);

                spinner_level = (Spinner)(activity).findViewById(R.id.spinner_level);
                spinner_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        setRank(spinner_level.getSelectedItemPosition());
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                //v.findViewById(R.id.settingBackgroundLayout).setVisibility(View.VISIBLE);
//                myStartActivity(ShowInfoActivity.class, mDataset.get(mainViewHolder.getAdapterPosition()));
            }
        });

        return mainViewHolder;
    }
    public void setRank(int selected_idx) {
        if(selected_idx==-1){
            iv_rank.setVisibility(View.GONE);
        }
        else{
            Glide.with(activity).load(img_rank[selected_idx]).centerCrop().override(500).into(iv_rank);
            iv_rank.setVisibility(View.VISIBLE);
        }
    }
    private void findPicture(String title) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child("users/" + user.getUid()+"/" + mode+level+title+"/profile.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                if(!uri.equals("")) {
                    Glide.with(activity).load(uri).centerCrop().override(500).into(iv_profile);
                    loaderLayout.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Glide.with(activity).load(R.drawable.ic_add_to_queue_black_24dp).centerCrop().override(500).into(iv_profile);
                loaderLayout.setVisibility(View.GONE);
                Log.e("MSG","There is no picture");
            }
        });
    }
    private void infoUpdate(String title) {
        //Date date = userList.size() == 0 || clear ? new Date() : userList.get(userList.size() - 1).getCreatedAt();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(user.getUid()).document(mode+level+title);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        et1.setText(document.get("n_Great").toString());
                        et2.setText(document.get("n_Good").toString());
                        et3.setText(document.get("n_Bad").toString());
                        et4.setText(document.get("n_Miss").toString());
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        iv_rank = (ImageView)cardView.findViewById(R.id.iv_rank);
        ImageView photoImageVIew = cardView.findViewById(R.id.photoImageVIew);
        TextView nameTextView = cardView.findViewById(R.id.nameTextView);
        SongInfo songInfo = mDataset.get(position);

        int t = songInfo.getUserLevel();
        setRank(t);

        if(mDataset.get(position).getAlbum() != null){
            Glide.with(activity).load(mDataset.get(position).getAlbum()).centerCrop().override(500).into(photoImageVIew);
        }
        nameTextView.setText(songInfo.getTitle());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(activity, c);
        activity.startActivityForResult(intent, 0);
    }
    private void myStartActivity(Class c, SongInfo songInfo) {
        Intent intent = new Intent(activity, c);
        intent.putExtra("album", songInfo.getAlbum());
        intent.putExtra("artist", songInfo.getArtist());
        intent.putExtra("bpm", songInfo.getBpm());
        intent.putExtra("level", songInfo.getLevel());
        intent.putExtra("title", songInfo.getTitle());
        intent.putExtra("category",songInfo.getCategory());
        activity.startActivityForResult(intent, 0);
    }
}