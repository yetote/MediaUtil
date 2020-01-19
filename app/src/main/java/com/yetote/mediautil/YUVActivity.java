package com.yetote.mediautil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yetote.mediautil.util.AndroidFileUtil;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class YUVActivity extends AppCompatActivity {
    private static final int PERMISSION_READ_FILE_CODE = 0x0001;
    private static final int FILE_SELECT_CODE = 0x1001;
    private Button chooseFileBtn;
    private Button parseBtn;
    private TextView pathTv;
    private Spinner spinner;
    private EditText widthEt, heightEt;
    private ImageView front, behind;
    private GLSurfaceView frameData;
    private ImageView helpIv;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuv);
        initView();

        chooseFileBtn.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_FILE_CODE);
            } else {
                chooseFile();
            }
        });
    }

    private void initView() {
        chooseFileBtn = findViewById(R.id.yuv_choose_btn);
        parseBtn = findViewById(R.id.yuv_parse_btn);
        pathTv = findViewById(R.id.yuv_path_et);
        spinner = findViewById(R.id.yuv_codec_name_spinner);
        helpIv = findViewById(R.id.yuv_codec_name_help);
        widthEt = findViewById(R.id.yuv_codec_name_width_et);
        heightEt = findViewById(R.id.yuv_codec_name_height_et);
        front = findViewById(R.id.yuv_frame_front);
        behind = findViewById(R.id.yuv_frame_behind);
        frameData = findViewById(R.id.yuv_frame_data);
        rv = findViewById(R.id.yuv_frame_rv);

        init();
    }

    private void init() {
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }

    private void chooseFile() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("video/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(i, "Select a File to Upload"), FILE_SELECT_CODE);
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

}