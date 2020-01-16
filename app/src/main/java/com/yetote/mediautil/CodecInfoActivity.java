package com.yetote.mediautil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.yetote.mediautil.util.DeviceUtil;

public class CodecInfoActivity extends AppCompatActivity {
    private static final String TAG = "CodecInfoActivity";
    String codecName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codec_info);

        Intent i = getIntent();
        codecName = i.getStringExtra("codecName");

        Log.e(TAG, "onCreate: "+ DeviceUtil.checkCodec(codecName).toString());

    }
}