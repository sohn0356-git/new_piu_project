package com.example.piu_project.adapter;

import android.app.Activity;
import android.content.Intent;
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

public class CategoryInfoAdapter extends RecyclerView.Adapter<CategoryInfoAdapter.MainViewHolder> {
    private ArrayList<SongInfo> mDataset;
    private Activity activity;

    static class MainViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        MainViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public CategoryInfoAdapter(Activity activity, ArrayList<SongInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
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
        if(mDataset.get(position).getAlbum() != null){
            Glide.with(activity).load(mDataset.get(position).getAlbum()).centerCrop().override(500).into(photoImageVIew);
        }
        tv_title.setText(songInfo.getTitle());
        tv_artist.setText(songInfo.getArtist());
        tv_bpm.setText(songInfo.getBpm());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void myStartActivity(Class c, SongInfo songInfo) {
        Intent intent = new Intent(activity, c);
        intent.putExtra("album", songInfo.getAlbum());
        intent.putExtra("artist", songInfo.getArtist());
        intent.putExtra("bpm", songInfo.getBpm());
        intent.putExtra("level", songInfo.getLevel());
        intent.putExtra("title", songInfo.getTitle());
        activity.startActivityForResult(intent, 0);
    }
}