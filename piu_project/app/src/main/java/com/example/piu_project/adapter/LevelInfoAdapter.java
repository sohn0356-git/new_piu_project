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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.piu_project.Activity.GalleryActivity;
import com.example.piu_project.Activity.ShowInfoActivity;
import com.example.piu_project.R;
import com.example.piu_project.SongInfo;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.piu_project.Util.INTENT_PATH;

public class LevelInfoAdapter extends RecyclerView.Adapter<LevelInfoAdapter.MainViewHolder> {
    private static final String TAG = "LevelInfoActivity";
    private ArrayList<SongInfo> mDataset;
    private Activity activity;
    private String level;
    private String mode;
    private ImageView iv_profile;
    private RelativeLayout loaderLayout;
    private FirebaseUser user;
    private FirebaseFirestore firebaseFirestore;
    private RelativeLayout settingBackgroundLayout;


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
                String title = ((TextView)(v.findViewById(R.id.nameTextView))).getText().toString();
                ((TextView)(activity).findViewById(R.id.tv_title)).setText(title);
                ((EditText)(activity).findViewById(R.id.editText1)).setText("");
                ((EditText)(activity).findViewById(R.id.editText2)).setText("");
                ((EditText)(activity).findViewById(R.id.editText3)).setText("");
                ((EditText)(activity).findViewById(R.id.editText4)).setText("");
                iv_profile = (ImageView)(activity.findViewById(R.id.iv_profile));
                activity.findViewById(R.id.bt_check).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        settingBackgroundLayout.setVisibility(View.GONE);
                    }
                });
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
                (activity.findViewById(R.id.settingBackgroundLayout)).bringToFront();
                (activity.findViewById(R.id.bt_gotoInfo)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myStartActivity(ShowInfoActivity.class, mDataset.get(mainViewHolder.getAdapterPosition()));
                    }
                });
                firebaseFirestore = FirebaseFirestore.getInstance();
                user = FirebaseAuth.getInstance().getCurrentUser();
                findPicture(title);

                //v.findViewById(R.id.settingBackgroundLayout).setVisibility(View.VISIBLE);
//                myStartActivity(ShowInfoActivity.class, mDataset.get(mainViewHolder.getAdapterPosition()));
            }
        });

        return mainViewHolder;
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

    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView photoImageVIew = cardView.findViewById(R.id.photoImageVIew);
        TextView nameTextView = cardView.findViewById(R.id.nameTextView);
        SongInfo songInfo = mDataset.get(position);
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