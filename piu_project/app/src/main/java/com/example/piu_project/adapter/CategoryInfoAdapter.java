package com.example.piu_project.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.piu_project.Activity.ShowInfoActivity;
import com.example.piu_project.R;
import com.example.piu_project.SongInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryInfoAdapter extends RecyclerView.Adapter<CategoryInfoAdapter.MainViewHolder> {
    private ArrayList<SongInfo> mDataset;
    private Activity activity;
    private Resources resources;
    private TypedArray album_info;

    static class MainViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        MainViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public CategoryInfoAdapter(Activity activity, ArrayList<SongInfo> myDataset, Resources resources) {
        this.resources = resources;
        this.mDataset = myDataset;
        this.activity = activity;
        album_info =resources.obtainTypedArray(R.array.album);
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @NonNull
    @Override
    public CategoryInfoAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_list, parent, false);
        final MainViewHolder mainViewHolder = new MainViewHolder(cardView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myStartActivity(ShowInfoActivity.class, mDataset.get(mainViewHolder.getAdapterPosition()));
            }
        });

        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView photoImageVIew = cardView.findViewById(R.id.photoImageVIew);
        TextView tv_title = cardView.findViewById(R.id.tv_title);
        TextView tv_artist = cardView.findViewById(R.id.tv_artist);
        TextView tv_bpm = cardView.findViewById(R.id.tv_bpm);
        SongInfo songInfo = mDataset.get(position);
        int song_id =mDataset.get(position).getSong_id();
        String resName = "@drawable/a_"+String.valueOf(song_id);
        String packName = activity.getPackageName(); // 패키지명
        int resID = activity.getResources().getIdentifier(resName, "drawable", packName);
        photoImageVIew.setImageResource(resID);
//        if(song_id != 0){
//            Glide.with(activity).load(album_info.getResourceId(song_id-1,-1)).centerCrop().override(500).into(photoImageVIew);
//        }
        tv_title.setText("Title : "+songInfo.getTitle());
        tv_artist.setText("Artist : "+songInfo.getArtist());
        tv_bpm.setText("BPM : "+songInfo.getBpm());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void myStartActivity(Class c, SongInfo songInfo) {
        Intent intent = new Intent(activity, c);
        intent.putExtra("song_id",songInfo.getSong_id());
        intent.putExtra("artist", songInfo.getArtist());
        intent.putExtra("bpm", songInfo.getBpm());
        intent.putExtra("level", songInfo.getLevel());
        intent.putExtra("title", songInfo.getTitle());
        intent.putExtra("category",songInfo.getCategory());
        for( HashMap.Entry<String,Object> elem : songInfo.getYoutubeLink().entrySet() ){
            for( HashMap.Entry<String,String> yLink : ((HashMap<String,String>)elem.getValue()).entrySet() ) {
                intent.putExtra(elem.getKey()+yLink.getKey(),yLink.getValue());
            }
        }
        activity.startActivityForResult(intent, 0);
    }
}