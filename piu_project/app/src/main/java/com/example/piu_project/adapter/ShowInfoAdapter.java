package com.example.piu_project.adapter;

import android.app.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.piu_project.R;
import com.example.piu_project.UserDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class ShowInfoAdapter extends RecyclerView.Adapter<ShowInfoAdapter.GalleryViewHolder> {
    private static final String TAG = "ShowInfoActivity";
    private ArrayList<UserDetail> mDataset;
    private Activity activity;
    private String[] level_source = new String[23];
    private Button bt_info;
    private RelativeLayout loaderLayout;
    private TypedArray s_button_on;
    private TypedArray s_button_off;
    private TypedArray d_button_on;
    private TypedArray d_button_off;
    private TypedArray co_button_on;
    private TypedArray co_button_off;
    private ImageView selectedImg;
    private String selectedLevel = "-1";
    private FirebaseUser user;
    private ImageView iv_info;
    private String title;
    private TypedArray img_rank;

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    private void pictureUpdate() {
        //Date date = userList.size() == 0 || clear ? new Date() : userList.get(userList.size() - 1).getCreatedAt();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(user.getUid()).document(selectedLevel+title);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if(document.get("photoUrl")==null || document.get("photoUrl").toString().equals("")){
//                            Glide.with(activity).load(R.drawable.ic_add_to_queue_black_24dp).override(500).into(iv_info);
                            iv_info.setVisibility(View.GONE);
                        } else{
                            iv_info.setVisibility(View.VISIBLE);
//                            showToast(activity,document.get("photoUrl").toString() );
                            Glide.with(activity).load(document.get("photoUrl").toString()).override(500).into(iv_info);
//                            Glide.with(activity).load(document.get("photoUrl").toString()).centerCrop().override(500).into(iv_profile);
                        }
                        loaderLayout.setVisibility(View.GONE);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        loaderLayout.setVisibility(View.GONE);
                        iv_info.setVisibility(View.GONE);
//                        Glide.with(activity).load(R.drawable.ic_add_to_queue_black_24dp).override(500).into(iv_info);
                        Log.d(TAG, "No such document");
                    }
                } else {
                    loaderLayout.setVisibility(View.GONE);
                    iv_info.setVisibility(View.GONE);
//                    Glide.with(activity).load(R.drawable.ic_add_to_queue_black_24dp).override(500).into(iv_info);
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    private void findPicture() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child("users/" + user.getUid()+"/" + selectedLevel+title+"/profile.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                if(!uri.equals("")) {
                    Glide.with(activity).load(uri).override(500).into(iv_info);
                    loaderLayout.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Glide.with(activity).load(R.drawable.ic_add_to_queue_black_24dp).override(500).into(iv_info);
                loaderLayout.setVisibility(View.GONE);
                Log.e("MSG","There is no picture");
            }
        });
    }

    public ShowInfoAdapter(Activity activity, ArrayList<UserDetail> myDataset, ImageView iv_info, String title, Resources resources) {
        mDataset = myDataset;
        this.activity = activity;
        this.iv_info = iv_info;
        this.title = title;
        s_button_on = resources.obtainTypedArray(R.array.s_btn_on);
        s_button_off = resources.obtainTypedArray(R.array.s_btn_off);
        d_button_on = resources.obtainTypedArray(R.array.d_btn_on);
        d_button_off = resources.obtainTypedArray(R.array.d_btn_off);
        img_rank = resources.obtainTypedArray(R.array.rank_img);
        user = FirebaseAuth.getInstance().getCurrentUser();
        iv_info.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public ShowInfoAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_level, parent, false);
        final GalleryViewHolder galleryViewHolder = new GalleryViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int level_i = 1;
                if (selectedLevel.charAt(0) == 'S'){
                    if(selectedLevel.charAt(1) != 'P') {
                        level_i = parseInt(selectedLevel.substring(1));
                    }
                } else if (selectedLevel.charAt(0) == 'D'){
                    if(selectedLevel.charAt(1) != 'P') {
                        level_i = parseInt(selectedLevel.substring(1));
                    }
                }else{

                }
                if (selectedLevel.charAt(0) == 'S') {
                    if(selectedLevel.charAt(1) != 'P') {
                        Glide.with(activity).load(s_button_off.getResourceId(level_i - 1, 0)).centerCrop().override(500).into(selectedImg);
                    }
                }
                if (selectedLevel.charAt(0) == 'D') {
                    if(selectedLevel.charAt(1) != 'P') {
                        Glide.with(activity).load(d_button_off.getResourceId(level_i - 1, 0)).centerCrop().override(500).into(selectedImg);
                    }
                }
                boolean newPick = !selectedLevel.equals(((TextView)v.findViewById(R.id.tv_level)).getText().toString());
                selectedImg = v.findViewById(R.id.imageView);
                String pLink = ((TextView)v.findViewById(R.id.tv_pLink)).getText().toString();
                String jLink = ((TextView)v.findViewById(R.id.tv_jLink)).getText().toString();
                String nLink = ((TextView)v.findViewById(R.id.tv_nLink)).getText().toString();
                selectedLevel = ((TextView)v.findViewById(R.id.tv_level)).getText().toString();
                ImageView iv_pLink = (activity).findViewById(R.id.iv_pLink);
                ImageView iv_jLink = (activity).findViewById(R.id.iv_jLink);
                ImageView iv_nLink = (activity).findViewById(R.id.iv_nLink);
                loaderLayout = activity.findViewById(R.id.loaderLyaout);
                if(newPick) {
                    loaderLayout.setVisibility(View.VISIBLE);
//                    findPicture();
                    pictureUpdate();
                    level_i = 1;
                    if (selectedLevel.charAt(0) == 'S'){
                        if(selectedLevel.charAt(1)!='P') {
                            level_i = parseInt(selectedLevel.substring(1));
                        }
                    } else if (selectedLevel.charAt(0) == 'D') {
                        if (selectedLevel.charAt(1) != 'P') {
                            level_i = parseInt(selectedLevel.substring(1));
                        }
                    }else{

                    }
                    if (selectedLevel.charAt(0) == 'S'){
                        if(selectedLevel.charAt(1)!='P') {
                            Glide.with(activity).load(s_button_on.getResourceId(level_i - 1, 0)).centerCrop().override(500).into(selectedImg);
                        }
                    }
                    if (selectedLevel.charAt(0) == 'D'){
                        if(selectedLevel.charAt(1)!='P') {
                            Glide.with(activity).load(d_button_on.getResourceId(level_i - 1, 0)).centerCrop().override(500).into(selectedImg);
                        }
                    }


                    if (!pLink.equals("")) {
                        iv_pLink.setVisibility(View.VISIBLE);
                        iv_pLink.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(pLink));
                                (activity).startActivity(intent);
                            }
                        });
                    } else {
                        iv_pLink.setVisibility(View.GONE);
                    }
                    if (!jLink.equals("")) {
                        iv_jLink.setVisibility(View.VISIBLE);
                        iv_jLink.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(jLink));
                                (activity).startActivity(intent);
                            }
                        });
                    } else {
                        iv_jLink.setVisibility(View.GONE);
                    }
                    if (!nLink.equals("")) {
                        iv_nLink.setVisibility(View.VISIBLE);
                        iv_nLink.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(nLink));
                                (activity).startActivity(intent);
                            }
                        });
                    } else {
                        iv_nLink.setVisibility(View.GONE);
                    }
                } else {
                    selectedLevel="None";
                    iv_info.setVisibility(View.GONE);
//                    Glide.with(activity).load(R.drawable.ic_add_to_queue_black_24dp).centerCrop().override(500).into(iv_info);
                    iv_pLink.setVisibility(View.GONE);
                    iv_jLink.setVisibility(View.GONE);
                    iv_nLink.setVisibility(View.GONE);
                }
            }
        });

        return galleryViewHolder;
    }


    private void infoUpdate(ImageView iv_rank, String level) {
        //Date date = userList.size() == 0 || clear ? new Date() : userList.get(userList.size() - 1).getCreatedAt();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(user.getUid()).document(level+title);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        int achievement = Integer.parseInt(document.get("achievement").toString());
                        setRank(iv_rank, achievement);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    setRank(iv_rank, -1);
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    public void setRank(ImageView iv_rank, int selected_idx) {
        if(selected_idx==-1){
            iv_rank.setVisibility(View.GONE);
        }
        else{
            if(selected_idx==0) {
                iv_rank.setScaleX(1.9f);
                iv_rank.setScaleY(1.9f);
            } else if(selected_idx==1) {
                iv_rank.setScaleX(1.5f);
                iv_rank.setScaleY(1.5f);
            }else {
                iv_rank.setScaleX(1.1f);
                iv_rank.setScaleY(1.1f);

            }
            Glide.with(activity).load(img_rank.getResourceId(selected_idx,0)).override(500).into(iv_rank);
            iv_rank.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final GalleryViewHolder holder, int position) {
        String level = mDataset.get(position).getLevel();
        CardView cardView = holder.cardView;
        ImageView imageView = cardView.findViewById(R.id.imageView);
        ImageView iv_rank = cardView.findViewById(R.id.iv_rank);
        iv_rank.bringToFront();
        infoUpdate(iv_rank,level);
//        if(position==0){
//            selectedImg=imageView;
//            selectedLevel="-1";
//        }
        int level_i = 1;
        if (level.charAt(0) == 'S'){
            if(level.charAt(1)!='P') {
                level_i = parseInt(level.substring(1));
            }
        } else if (level.charAt(0) == 'D'){
            if(level.charAt(1)!='P') {
                level_i = parseInt(level.substring(1));
            }
        }else{

        }
        if (level.charAt(0) == 'S'){
            if(level.charAt(1)!='P') {
                Glide.with(activity).load(s_button_off.getResourceId(level_i - 1, 0)).centerCrop().override(500).into(imageView);
            }
        }
        if (level.charAt(0) == 'D'){
            if(level.charAt(1)!='P') {
                Glide.with(activity).load(d_button_off.getResourceId(level_i - 1, 0)).centerCrop().override(500).into(imageView);
            }
        }
        ((TextView)cardView.findViewById(R.id.tv_unlockCondition)).setText(mDataset.get(position).getunlockCondition());
        ((TextView)cardView.findViewById(R.id.tv_pLink)).setText(mDataset.get(position).getYoutubeLinkP());
        ((TextView)cardView.findViewById(R.id.tv_jLink)).setText(mDataset.get(position).getYoutubeLinkJ());
        ((TextView)cardView.findViewById(R.id.tv_nLink)).setText(mDataset.get(position).getYoutubeLinkN());
        ((TextView)cardView.findViewById(R.id.tv_level)).setText(level);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}