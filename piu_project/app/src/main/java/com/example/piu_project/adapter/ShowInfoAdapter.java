package com.example.piu_project.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.piu_project.R;
import com.google.firebase.database.core.utilities.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.piu_project.Util.INTENT_PATH;

public class ShowInfoAdapter extends RecyclerView.Adapter<ShowInfoAdapter.GalleryViewHolder> {
    private List<Pair<String, String>> mDataset;
    private Activity activity;
    private String[] level_source = new String[23];
    private Button bt_info;

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public ShowInfoAdapter(Activity activity, List<Pair<String, String>> myDataset, Button bt_info) {
        mDataset = myDataset;
        this.activity = activity;
        this.bt_info = bt_info;
    }

    @NonNull
    @Override
    public ShowInfoAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_level, parent, false);
        final GalleryViewHolder galleryViewHolder = new GalleryViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = v.findViewById(R.id.imageView);
                Glide.with(activity).load(R.drawable.s18).centerCrop().override(500).into(imageView);
                bt_info.setBackground(ContextCompat.getDrawable(activity, R.drawable.userinfo));
            }
        });

        return galleryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final GalleryViewHolder holder, int position) {
        int a=R.drawable.s16;
        CardView cardView = holder.cardView;
        ImageView imageView = cardView.findViewById(R.id.imageView);
        @SuppressLint("RestrictedApi") String ab = mDataset.get(position).getFirst();
        Glide.with(activity).load(a).centerCrop().override(500).into(imageView);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}