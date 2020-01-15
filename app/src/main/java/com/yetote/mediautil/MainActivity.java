package com.yetote.mediautil;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.yetote.mediautil.adapter.ViewPagerAdapter;
import com.yetote.mediautil.fragment.HwFragment;
import com.yetote.mediautil.fragment.SettingFragment;
import com.yetote.mediautil.fragment.UtilFragment;

import java.util.ArrayList;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends AppCompatActivity {
    private ViewPager vp;
    private TabLayout tl;
    private FragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {
        vp = findViewById(R.id.vp);
        tl = findViewById(R.id.tl);
        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(new HwFragment());
        fragmentArrayList.add(new SettingFragment());
        fragmentArrayList.add(new UtilFragment());

        String[] title = new String[]{
                "硬件", "功能", "设置"
        };
        tl.addTab(tl.newTab().setText(title[0]), true);
        tl.addTab(tl.newTab().setText(title[1]));
        tl.addTab(tl.newTab().setText(title[2]));
        tl.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        tl.setTabRippleColorResource(R.color.colorAccent);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragmentArrayList, title);
        vp.setAdapter(adapter);
        tl.setupWithViewPager(vp);
    }
}
