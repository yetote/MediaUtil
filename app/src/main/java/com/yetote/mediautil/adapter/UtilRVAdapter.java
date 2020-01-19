package com.yetote.mediautil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yetote.mediautil.R;
import com.yetote.mediautil.interfaces.OnClick;

import java.util.ArrayList;
import java.util.List;

public class UtilRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<String> list;
    private OnClick<String> onClick;

    public void setOnClick(OnClick<String> onClick) {
        this.onClick = onClick;
    }

    public UtilRVAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public TextView getTextView() {
            return textView;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_util_tv);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_util_rv, parent, false);
        v.setOnClickListener(V1 -> onClick.onClickListener((String) v.getTag()));
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder vh = (MyViewHolder) holder;
        vh.getTextView().setText(list.get(position));
        vh.itemView.setTag(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
