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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.piu_project.R;
import com.example.piu_project.UserDetail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ShowInfoAdapter extends RecyclerView.Adapter<ShowInfoAdapter.GalleryViewHolder> {
    private ArrayList<UserDetail> mDataset;
    private Activity activity;
    private String[] level_source = new String[23];
    private Button bt_info;
    private TypedArray s_button_on;
    private TypedArray s_button_off;
    private ImageView selectedImg;
    private String selectedLevel;
    private FirebaseUser user;
    private ImageView iv_info;
    private int[] img_rank = {R.drawable.level_s,R.drawable.level_s,R.drawable.level_a,R.drawable.level_a,R.drawable.level_a,R.drawable.level_a,R.drawable.level_a,R.drawable.level_a};
    private ImageView iv_rank;

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }


    private void findPicture(String title,String mode, String level) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child("users/" + user.getUid()+"/" + mode+level+title+"/profile.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                if(!uri.equals("")) {
                    Glide.with(activity).load(uri).centerCrop().override(500).into(iv_info);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Glide.with(activity).load(R.drawable.ic_add_to_queue_black_24dp).centerCrop().override(500).into(iv_info);
                Log.e("MSG","There is no picture");
            }
        });
    }

    public ShowInfoAdapter(Activity activity, ArrayList<UserDetail> myDataset, ImageView iv_info, Resources resources) {
        mDataset = myDataset;
        this.activity = activity;
        this.iv_info = iv_info;
        s_button_on = resources.obtainTypedArray(R.array.s_btn_on);
        s_button_off = resources.obtainTypedArray(R.array.s_btn_off);
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

                iv_rank = (ImageView)cardView.findViewById(R.id.iv_rank);
                int level_i = 1;
                if (selectedLevel.charAt(0) == 'S') {
                    level_i = Integer.parseInt(selectedLevel.substring(1));
                } else {
                    if (selectedLevel.charAt(0) == 'D' && selectedLevel.charAt(1) != 'P') {
                        level_i = Integer.parseInt(selectedLevel.substring(1));
                    }
                }
                if (selectedLevel.charAt(0) == 'S') {
                    Glide.with(activity).load(s_button_off.getResourceId(level_i - 1, 0)).centerCrop().override(500).into(selectedImg);
                }


                selectedImg = v.findViewById(R.id.imageView);
                String eLink = ((TextView)v.findViewById(R.id.tv_eLink)).getText().toString();
                String jLink = ((TextView)v.findViewById(R.id.tv_jLink)).getText().toString();
                String nLink = ((TextView)v.findViewById(R.id.tv_nLink)).getText().toString();
                selectedLevel = ((TextView)v.findViewById(R.id.tv_level)).getText().toString();
                ImageView iv_eLink = (activity).findViewById(R.id.iv_eLink);
                ImageView iv_jLink = (activity).findViewById(R.id.iv_jLink);
                ImageView iv_nLink = (activity).findViewById(R.id.iv_nLink);

                level_i = 1;
                if (selectedLevel.charAt(0) == 'S') {
                    level_i = Integer.parseInt(selectedLevel.substring(1));
                } else {
                    if (selectedLevel.charAt(0) == 'D' && selectedLevel.charAt(1) != 'P') {
                        level_i = Integer.parseInt(selectedLevel.substring(1));
                    }
                }
                if (selectedLevel.charAt(0) == 'S') {
                    Glide.with(activity).load(s_button_on.getResourceId(level_i - 1, 0)).centerCrop().override(500).into(selectedImg);
                }


                if(!eLink.equals("")) {
                    iv_eLink.setVisibility(View.VISIBLE);
                }else{
                    iv_eLink.setVisibility(View.GONE);
                }
                if(!jLink.equals("")) {
                    iv_jLink.setVisibility(View.VISIBLE);
                }else{
                    iv_jLink.setVisibility(View.GONE);
                }
                if(!nLink.equals("")) {
                    iv_nLink.setVisibility(View.VISIBLE);
                }else{
                    iv_nLink.setVisibility(View.GONE);
                }
            }
        });

        return galleryViewHolder;
    }

    public void setRank(int selected_idx) {
        if(selected_idx==-1){
            iv_rank.setVisibility(View.GONE);
        }
        else{
            Glide.with(activity).load(img_rank[selected_idx]).centerCrop().override(500).into(iv_rank);
            iv_rank.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final GalleryViewHolder holder, int position) {
        String level = mDataset.get(position).getLevel();
        CardView cardView = holder.cardView;
        ImageView imageView = cardView.findViewById(R.id.imageView);
        cardView.findViewById(R.id.iv_rank).bringToFront();
        if(position==0){
            selectedImg=imageView;
            selectedLevel=level;
        }
        int level_i = 1;
        if (level.charAt(0) == 'S') {
            level_i = Integer.parseInt(level.substring(1));
        } else {
            if (level.charAt(0) == 'D' && level.charAt(1) != 'P') {
                level_i = Integer.parseInt(level.substring(1));
            }
        }
        if (level.charAt(0) == 'S') {
            Glide.with(activity).load(s_button_off.getResourceId(level_i - 1, 0)).centerCrop().override(500).into(imageView);
        }


        String ab = mDataset.get(position).getAchivement();

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