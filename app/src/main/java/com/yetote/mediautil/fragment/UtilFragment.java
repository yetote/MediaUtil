package com.yetote.mediautil.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yetote.mediautil.EncodeAudioActivity;
import com.yetote.mediautil.R;
import com.yetote.mediautil.YUVActivity;
import com.yetote.mediautil.adapter.UtilRVAdapter;

import java.util.ArrayList;

public class UtilFragment extends Fragment {
    private ArrayList<String> list;
    private RecyclerView rv;
    private UtilRVAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_util_layout, container, false);
        rv = v.findViewById(R.id.fragment_util_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        list = new ArrayList<>();
        list.add("YUV");
        list.add("Encode Audio");
        adapter = new UtilRVAdapter(getContext(), list);
        adapter.setOnClick(s -> {
            switch (s) {
                case "YUV":
                    startActivity(new Intent(getContext(), YUVActivity.class));
                    break;
                case "Encode Audio":
                    startActivity(new Intent(getContext(), EncodeAudioActivity.class));
                    break;
                default:
                    Toast.makeText(getContext(), "暂不支持此功能", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        rv.setAdapter(adapter);
        return v;
    }
}
