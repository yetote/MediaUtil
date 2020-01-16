package com.yetote.mediautil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yetote.mediautil.R;
import com.yetote.mediautil.bean.HwBean;
import com.yetote.mediautil.interfaces.OnClick;

import java.util.ArrayList;
import java.util.List;

public class HwRvAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<HwBean> list;
    private OnClick<HwBean> onClick;

    public HwRvAdapter(Context context, List<HwBean> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClick(OnClick<HwBean> onClick) {
        this.onClick = onClick;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView codecNameTv;
        TextView canonicalNameTv;
        TextView supportedTypesTv;
        TextView isAliasTv;
        TextView isEncoderTv;
        TextView isHardwareAcceleratedTv;
        TextView isSoftwareOnlyTv;
        TextView isVendorTv;

        public TextView getCodecNameTv() {
            return codecNameTv;
        }

        public TextView getCanonicalNameTv() {
            return canonicalNameTv;
        }

        public TextView getSupportedTypesTv() {
            return supportedTypesTv;
        }

        public TextView getIsAliasTv() {
            return isAliasTv;
        }

        public TextView getIsEncoderTv() {
            return isEncoderTv;
        }

        public TextView getIsHardwareAcceleratedTv() {
            return isHardwareAcceleratedTv;
        }

        public TextView getIsSoftwareOnlyTv() {
            return isSoftwareOnlyTv;
        }

        public TextView getIsVendorTv() {
            return isVendorTv;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            codecNameTv = itemView.findViewById(R.id.item_hw_title_codecName);
            canonicalNameTv = itemView.findViewById(R.id.item_hw_content_canonicalName);
            supportedTypesTv = itemView.findViewById(R.id.item_hw_content_supportedTypes);
            isAliasTv = itemView.findViewById(R.id.item_hw_content_isAlias);
            isEncoderTv = itemView.findViewById(R.id.item_hw_content_isEncoder);
            isHardwareAcceleratedTv = itemView.findViewById(R.id.item_hw_content_isHardwareAccelerated);
            isSoftwareOnlyTv = itemView.findViewById(R.id.item_hw_content_isSoftwareOnly);
            isVendorTv = itemView.findViewById(R.id.item_hw_content_isVendor);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_hw_rv, parent, false);
        v.setOnClickListener(v1 -> onClick.onClickListener(list.get((Integer) v.getTag())));
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder vh = (MyViewHolder) holder;
        vh.getCodecNameTv().setText((list.get(position).getCodecName()));
        vh.getCanonicalNameTv().setText(String.format(context.getResources().getString(R.string.codec_info), "基础编解码器名称", list.get(position).getCanonicalName()));
        vh.getSupportedTypesTv().setText(String.format(context.getResources().getString(R.string.codec_info), "支持的媒体类型", list.get(position).getSupportedTypes()));
        vh.getIsAliasTv().setText(String.format(context.getResources().getString(R.string.codec_info), "是否为别名", list.get(position).getIsAlias()));
        vh.getIsEncoderTv().setText(String.format(context.getResources().getString(R.string.codec_info), "是否为编码器", list.get(position).getIsEncoder()));
        vh.getIsHardwareAcceleratedTv().setText(String.format(context.getResources().getString(R.string.codec_info), "是否支持硬件加速", list.get(position).getIsHardwareAccelerated()));
        vh.getIsSoftwareOnlyTv().setText(String.format(context.getResources().getString(R.string.codec_info), "是否为软编解码器", list.get(position).getIsSoftwareOnly()));
        vh.getIsVendorTv().setText(String.format(context.getResources().getString(R.string.codec_info), "是否为供应商提供", list.get(position).getIsVendor()));
        vh.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
