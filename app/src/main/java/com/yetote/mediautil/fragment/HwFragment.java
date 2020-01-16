package com.yetote.mediautil.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yetote.mediautil.CodecInfoActivity;
import com.yetote.mediautil.adapter.HwRvAdapter;
import com.yetote.mediautil.R;
import com.yetote.mediautil.bean.HwBean;
import com.yetote.mediautil.interfaces.OnClick;
import com.yetote.mediautil.util.DeviceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.media.MediaCodecList.ALL_CODECS;

public class HwFragment extends Fragment {

    private RecyclerView rv;
    private HwRvAdapter adapter;
    private List<HwBean> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hw_layout, container, false);

        rv = v.findViewById(R.id.fragment_hw_rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = new ArrayList<>();
        list = Arrays.asList(DeviceUtil.checkAllCodec());
        adapter = new HwRvAdapter(getActivity(), list);
        adapter.setOnClick(new OnClick<HwBean>() {
            @Override
            public void onClickListener(HwBean hwBean) {
                Intent i = new Intent(getActivity(), CodecInfoActivity.class);
                i.putExtra("codecName", hwBean.getCodecName());
                startActivity(i);
            }
        });
        rv.setAdapter(adapter);


//        DeviceUtil.checkCodec(ALL_CODECS);

        return v;
    }
}
