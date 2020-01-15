package com.yetote.mediautil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yetote.mediautil.bean.HwBean;

import java.util.ArrayList;

public class HwRvAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<HwBean> list;

    public HwRvAdapter(Context context, ArrayList<HwBean> list) {
        this.context = context;
        this.list = list;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView content;

        public TextView getTitle() {
            return title;
        }

        public TextView getContent() {
            return content;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_hw_title_tv);
            content = itemView.findViewById(R.id.item_hw_content_tv);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_hw_rv, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder vh = (MyViewHolder) holder;
        vh.getTitle().setText(list.get(position).getTitle());
        vh.getContent().setText(list.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
