package com.example.piu_project.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
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
    private Resources resources;
    private TypedArray img_rank;
    private String[] rank =  {"SSS", "SS", "S", "A (Break on)", "A (Break off)", "B (Break on)", "B (Break off)", "C (Break on)", "C (Break off)", "D(Break on)", "D (Break off)", "F or Game Over", "No Play"};
    private TypedArray album_info;
    private int [] difficultyCount={0,0,0,0,0,0,0,0};
    private int [] difficultyColor={Color.parseColor("#14148C"),Color.parseColor("#7878E1"),Color.parseColor("#73E1E1"),Color.parseColor("#5CEEE6"),
            Color.parseColor("#73EA88"),Color.parseColor("#63CC63"),Color.parseColor("#FFD732"),Color.parseColor("#FFFF0000"),};

    static class MainViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        MainViewHolder(CardView v) {
            super(v);
            cardView = v;

        }

    }

    public LevelInfoAdapter(Activity activity, ArrayList<SongInfo> myDataset, String mode, String level, Resources resources, RelativeLayout settingBackgroundLayout) {
        this.resources = resources;
        this.mDataset = myDataset;
        this.activity = activity;
        this.mode = mode;
        this.level = level;
        album_info =resources.obtainTypedArray(R.array.album);
        img_rank = resources.obtainTypedArray(R.array.rank_img);
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        this.settingBackgroundLayout = settingBackgroundLayout;
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
                title = ((TextView) (v.findViewById(R.id.nameTextView))).getText().toString();
                if (!title.equals("")) {
                    et1 = (EditText) (activity).findViewById(R.id.editText1);
                    et2 = (EditText) (activity).findViewById(R.id.editText2);
                    et3 = (EditText) (activity).findViewById(R.id.editText3);
                    et4 = (EditText) (activity).findViewById(R.id.editText4);
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    et1.setText("");
                    et2.setText("");
                    et3.setText("");
                    et4.setText("");
                    ((TextView) (activity).findViewById(R.id.tv_title)).setText(title);
                    iv_profile = (ImageView) (activity.findViewById(R.id.iv_profile));
//                settingBackgroundLayout = (activity.findViewById(R.id.settingBackgroundLayout));
                    settingBackgroundLayout.setVisibility(View.VISIBLE);
                    loaderLayout = activity.findViewById(R.id.loaderLyaout);
                    loaderLayout.setVisibility(View.VISIBLE);
                    settingBackgroundLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            settingBackgroundLayout.setVisibility(View.GONE);
                        }
                    });
                    iv_rank = (ImageView) cardView.findViewById(R.id.iv_rank);
                    settingBackgroundLayout.bringToFront();
                    settingBackgroundLayout.setGravity(Gravity.CENTER);
                    (activity.findViewById(R.id.bt_gotoInfo)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myStartActivity(ShowInfoActivity.class, mDataset.get(mainViewHolder.getAdapterPosition()));
                        }
                    });
                    infoUpdate(title);
                    findPicture(title);
                    //v.findViewById(R.id.settingBackgroundLayout).setVisibility(View.VISIBLE);
//                myStartActivity(ShowInfoActivity.class, mDataset.get(mainViewHolder.getAdapterPosition()));
                }
            }
        });

        return mainViewHolder;
    }
    public void setRank(int selected_idx) {
        if(selected_idx==-1){
            iv_rank.setVisibility(View.GONE);
        }
        else{
            Glide.with(activity).load(img_rank.getResourceId(selected_idx,0)).centerCrop().override(500).into(iv_rank);
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
        SongInfo songInfo = mDataset.get(position);
        int selected_difficulty = Integer.parseInt(songInfo.getDifficulty());
        if(position==0){
            for(int i=0;i<8;i++){
                difficultyCount[i]=0;
            }
        }
        difficultyCount[selected_difficulty]++;
        if(position+1!=getItemCount() && (!mDataset.get(position+1).getDifficulty().equals(songInfo.getDifficulty())&&difficultyCount[selected_difficulty]%5!=0)){
            mDataset.add(position+1,new SongInfo(String.valueOf(selected_difficulty)));
        } else if(position+1==getItemCount()){
            while(mDataset.size()%5!=0) {
                mDataset.add(position + 1, new SongInfo(String.valueOf(selected_difficulty)));
            }
        }
        cardView.setBackgroundColor(difficultyColor[selected_difficulty]);
        if(songInfo.getTitle().equals("blank")){
            cardView.findViewById(R.id.linearLayout).setVisibility(View.GONE);
        } else {
            TextView nameTextView = cardView.findViewById(R.id.nameTextView);
            iv_rank = (ImageView)cardView.findViewById(R.id.iv_rank);
            ImageView photoImageVIew = cardView.findViewById(R.id.photoImageVIew);

            int t = songInfo.getUserLevel();
            setRank(t);

            int song_id = mDataset.get(position).getSong_id();
            if (song_id != 0) {
                Glide.with(activity).load(album_info.getResourceId(song_id - 1, 0)).centerCrop().override(500).into(photoImageVIew);
            }
            String songTitle = songInfo.getTitle();
            boolean fullSongORshortCut = false;
            if(songInfo.getCategory().equals("Full Song") || songInfo.getCategory().equals("Short Cut") ){
                songTitle = songTitle.substring(0,songTitle.length()-14);
                fullSongORshortCut=true;
            }

                int newLineCnt = 0;
                int songTitleLength = 0;
                int lastSpacePos = -1;

                for (int i = 0; i < songTitle.length(); i++) {
                    if (songTitle.charAt(i) > 'z') {
                        songTitleLength += 2;
                    } else {
                        if (songTitle.charAt(i) == ' ') {
                            lastSpacePos = i;
                        }
                        songTitleLength++;
                    }
                    if (songTitleLength > 15) {
                        songTitle = songTitle.substring(0, lastSpacePos) + "\n" + songTitle.substring(lastSpacePos + 1);
                        songTitleLength = 0;
                        newLineCnt++;
                    }
                }
                if(fullSongORshortCut){
                    songTitle+="\ndfas";
                }
                if (newLineCnt > 2) {
                    nameTextView.setText(songInfo.getTitle());
                    nameTextView.setTextSize(8);
                } else {
                    nameTextView.setText(songTitle);
                }

//            nameTextView.setText("포 시즌스 오브 론리네스 verβ (풀송)");
//            nameTextView.setBreakStrategy()
        }
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
        intent.putExtra("song_id",songInfo.getSong_id());
        intent.putExtra("artist", songInfo.getArtist());
        intent.putExtra("bpm", songInfo.getBpm());
        intent.putExtra("level", songInfo.getLevel());
        intent.putExtra("title", songInfo.getTitle());
        intent.putExtra("category",songInfo.getCategory());
        for( HashMap.Entry<String,HashMap<String,String>> elem : songInfo.getYoutubeLink().entrySet() ){
            for( HashMap.Entry<String,String> yLink : elem.getValue().entrySet() ) {
                intent.putExtra(elem.getKey()+yLink.getKey(),yLink.getValue());
            }
        }
        activity.startActivityForResult(intent, 0);
    }
}