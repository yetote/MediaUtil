package com.yetote.mediautil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yetote.mediautil.R;
import com.yetote.mediautil.util.yuvutil.YUVUtil;

import java.util.ArrayList;

public class YUVRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<byte[]> list;
    private int width, height;

    public YUVRVAdapter(Context context, ArrayList<byte[]> list, int w, int h) {
        this.context = context;
        this.list = list;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv;

        public ImageView getIv() {
            return iv;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.item_yuv_iv);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_yuv_rv, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        Glide.with(context).load(YUVUtil.yuv2bitmap(width, height, list.get(position))).into(viewHolder.getIv());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
