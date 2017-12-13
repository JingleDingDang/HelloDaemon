package com.xdandroid.sample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImgViewHolder> {

    private Context context;
    private ArrayList<String> mData;

    public ImageAdapter(Context context) {
        this.context = context;
    }

    public void setmData(ArrayList<String> mData) {
        if (mData == null) mData = new ArrayList<>();
        this.mData = mData;
        notifyDataSetChanged();
    }

    @Override
    public ImgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImgViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ImgViewHolder holder, int position) {
        String string = mData.get(position);
        Glide.with(context)
                .load(string)
//                .centerCrop()
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ImgViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLinearLayout;
        private ImageView mImageView;

        public ImgViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv);
        }
    }

}
