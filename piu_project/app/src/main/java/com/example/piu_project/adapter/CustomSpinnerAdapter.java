package com.example.piu_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.piu_project.R;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    int[] spinnerImages;
    Context mContext;

    public CustomSpinnerAdapter(@NonNull Context context, int[] images) {
        super(context, R.layout.spinner_row);

        this.spinnerImages = images;
        this.mContext = context;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return spinnerImages.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder mViewHolder = new ViewHolder();

        if (convertView == null) {

            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.spinner_row, parent, false);

            mViewHolder.mImage = (ImageView) convertView.findViewById(R.id.imageview_spinner_image);
            convertView.setTag(mViewHolder);

        } else {

            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.mImage.setImageResource(spinnerImages[position]);

        return convertView;
    }

    private static class ViewHolder {

        ImageView mImage;
    }
}