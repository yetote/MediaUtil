package com.yetote.mediautil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yetote.mediautil.bean.HwBean;
import com.yetote.mediautil.util.AndroidFileUtil;
import com.yetote.mediautil.util.DeviceUtil;
import com.yetote.mediautil.util.FileUtil;
import com.yetote.mediautil.util.HardWareCodec;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class EncodeAudioActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EncodeAudioActivity";
    private static final int PERMISSION_READ_FILE_CODE = 0x0001;
    private static final int FILE_SELECT_CODE = 0x1001;

    private Button chooseFileBtn;
    private TextView pathTv;
    private Button parseBtn;
    private Button chooseFormatBtn, chooseCodecBtn;

    private static int AUDIO_FORMAT = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode_audio);
        initView();
        click();
    }

    private void initView() {
        chooseFileBtn = findViewById(R.id.layout_choose_file_choose_btn);
        pathTv = findViewById(R.id.layout_choose_file_path_et);
        parseBtn = findViewById(R.id.encode_audio_parse_btn);
        chooseFormatBtn = findViewById(R.id.encode_audio_choose_format_btn);
        chooseCodecBtn = findViewById(R.id.encode_audio_choose_codec_btn);
    }

    private void click() {
        chooseFileBtn.setOnClickListener(this);
        parseBtn.setOnClickListener(this);
        chooseFormatBtn.setOnClickListener(this);
    }

    private void chooseFile() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("file/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(i, "Select a File to Upload"), FILE_SELECT_CODE);
    }

    public void clear() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_choose_file_choose_btn:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_READ_FILE_CODE);
                } else {
                    chooseFile();
                }
                break;
            case R.id.encode_audio_choose_format_btn:
                List<HwBean> list = DeviceUtil.checkAllCodec("audio");
                Set<String> mediaTypeSet = new HashSet<>();
                for (int i = 0; i < list.size(); i++) {
                    mediaTypeSet.add(list.get(i).getSupportedTypes());
                }

                break;
            case R.id.encode_audio_choose_codec_btn:
                break;

            case R.id.encode_audio_parse_btn:

                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    String path = AndroidFileUtil.getRealPathFromUri(this, data.getData());
                    pathTv.setText(path);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ_FILE_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseFile();
                } else {
                    Toast.makeText(this, "权限被拒绝，无法读取文件", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        FileUtil.close();
        super.onDestroy();
    }

}