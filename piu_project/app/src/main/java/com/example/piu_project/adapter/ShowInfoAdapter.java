package com.example.piu_project.adapter;

import android.app.Activity;

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
    private ImageView selectedImg;
    private String selectedLevel;
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


    private void findPicture() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child("users/" + user.getUid()+"/" + selectedLevel+title+"/profile.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                if(!uri.equals("")) {
                    Glide.with(activity).load(uri).centerCrop().override(500).into(iv_info);
                    loaderLayout.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Glide.with(activity).load(R.drawable.ic_add_to_queue_black_24dp).centerCrop().override(500).into(iv_info);
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
        img_rank = resources.obtainTypedArray(R.array.rank_img);
        user = FirebaseAuth.getInstance().getCurrentUser();
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
                if (selectedLevel.charAt(0) == 'S') {
                    level_i = parseInt(selectedLevel.substring(1));
                } else {
                    if (selectedLevel.charAt(0) == 'D' && selectedLevel.charAt(1) != 'P') {
                        level_i = parseInt(selectedLevel.substring(1));
                    }
                }
                if (selectedLevel.charAt(0) == 'S') {
                    Glide.with(activity).load(s_button_off.getResourceId(level_i - 1, 0)).centerCrop().override(500).into(selectedImg);
                }
                boolean newPick = !selectedLevel.equals(((TextView)v.findViewById(R.id.tv_level)).getText().toString());
                selectedImg = v.findViewById(R.id.imageView);
                String eLink = ((TextView)v.findViewById(R.id.tv_eLink)).getText().toString();
                String jLink = ((TextView)v.findViewById(R.id.tv_jLink)).getText().toString();
                String nLink = ((TextView)v.findViewById(R.id.tv_nLink)).getText().toString();
                selectedLevel = ((TextView)v.findViewById(R.id.tv_level)).getText().toString();
                ImageView iv_eLink = (activity).findViewById(R.id.iv_eLink);
                ImageView iv_jLink = (activity).findViewById(R.id.iv_jLink);
                ImageView iv_nLink = (activity).findViewById(R.id.iv_nLink);
                loaderLayout = activity.findViewById(R.id.loaderLyaout);
                if(newPick) {
                    loaderLayout.setVisibility(View.VISIBLE);
                    findPicture();

                    level_i = 1;
                    if (selectedLevel.charAt(0) == 'S' && selectedLevel.charAt(1)!='P') {
                        level_i = parseInt(selectedLevel.substring(1));
                    } else {
                        if (selectedLevel.charAt(0) == 'D' && selectedLevel.charAt(1) != 'P') {
                            level_i = parseInt(selectedLevel.substring(1));
                        }
                    }
                    if (selectedLevel.charAt(0) == 'S' && selectedLevel.charAt(1)!='P') {
                        Glide.with(activity).load(s_button_on.getResourceId(level_i - 1, 0)).centerCrop().override(500).into(selectedImg);
                    }


                    if (!eLink.equals("")) {
                        iv_eLink.setVisibility(View.VISIBLE);
                    } else {
                        iv_eLink.setVisibility(View.GONE);
                    }
                    if (!jLink.equals("")) {
                        iv_jLink.setVisibility(View.VISIBLE);
                    } else {
                        iv_jLink.setVisibility(View.GONE);
                    }
                    if (!nLink.equals("")) {
                        iv_nLink.setVisibility(View.VISIBLE);
                    } else {
                        iv_nLink.setVisibility(View.GONE);
                    }
                } else {
                    selectedLevel="None";
                    Glide.with(activity).load(R.drawable.ic_add_to_queue_black_24dp).centerCrop().override(500).into(iv_info);
                    iv_eLink.setVisibility(View.GONE);
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
            Glide.with(activity).load(img_rank.getResourceId(selected_idx,0)).centerCrop().override(500).into(iv_rank);
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
        if(position==0){
            selectedImg=imageView;
            selectedLevel=level;
        }
        int level_i = 1;
        if (level.charAt(0) == 'S' && level.charAt(1)!='P') {
                level_i = parseInt(level.substring(1));
        } else {
            if (level.charAt(0) == 'D' && level.charAt(1) != 'P') {
                level_i = parseInt(level.substring(1));
            }
        }
        if (level.charAt(0) == 'S' && level.charAt(1)!='P') {
            Glide.with(activity).load(s_button_off.getResourceId(level_i - 1, 0)).centerCrop().override(500).into(imageView);
        }

        ((TextView)cardView.findViewById(R.id.tv_eLink)).setText(mDataset.get(position).getYoutubeLinkE());
        ((TextView)cardView.findViewById(R.id.tv_jLink)).setText(mDataset.get(position).getYoutubeLinkJ());
        ((TextView)cardView.findViewById(R.id.tv_nLink)).setText(mDataset.get(position).getYoutubeLinkN());
        ((TextView)cardView.findViewById(R.id.tv_level)).setText(level);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}