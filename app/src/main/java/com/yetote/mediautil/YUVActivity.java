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
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yetote.mediautil.glsl.YUVRenderer;
import com.yetote.mediautil.util.AndroidFileUtil;
import com.yetote.mediautil.util.FileUtil;
import com.yetote.mediautil.util.yuvutil.YUVUtil;

import java.nio.channels.FileChannel;
import java.util.Arrays;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static com.yetote.mediautil.util.FileUtil.FILE_STATE_CLOSED;
import static com.yetote.mediautil.util.FileUtil.FILE_STATE_END;
import static com.yetote.mediautil.util.FileUtil.FILE_STATE_PREPARED;
import static com.yetote.mediautil.util.FileUtil.FILE_STATE_READING;
import static com.yetote.mediautil.util.FileUtil.FILE_STATE_START;
import static com.yetote.mediautil.util.FileUtil.FILE_STATE_UN_EXIST;
import static com.yetote.mediautil.util.FileUtil.FILE_STATE_UN_PREPARED;

public class YUVActivity extends AppCompatActivity {
    private static final int PERMISSION_READ_FILE_CODE = 0x0001;
    private static final int FILE_SELECT_CODE = 0x1001;
    private Button chooseFileBtn;
    private Button parseBtn;
    private TextView pathTv;
    private Spinner codecNameSpinner, showTypeSpinner;
    private EditText widthEt, heightEt;
    private ImageView front, behind;
    private GLSurfaceView frameData;
    private ImageView helpIv;
    private RecyclerView rv;
    private static final String TAG = "YUVActivity";
    private YUVRenderer renderer;
    private YUVUtil.YUV_TYPE yuvFlag;
    private int showFlag = 0;
    byte[] ydata;
    byte[] udata;
    byte[] vdata;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuv);
        initView();
        MediaPlayer
        chooseFileBtn.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_READ_FILE_CODE);
            } else {
                chooseFile();
            }
        });

        parseBtn.setOnClickListener(v -> {
            if (pathTv.getText().toString().isEmpty()) {
                Toast.makeText(this, "请选择文件", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pathTv.getText().toString().endsWith("yuv")) {
                Toast.makeText(this, "该功能仅支持YUV文件", Toast.LENGTH_SHORT).show();
                return;
            }

            if (widthEt.getText().toString().isEmpty() || heightEt.getText().toString().isEmpty()) {
                Toast.makeText(this, "请填写视频宽高", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!renderer.prepare(Integer.parseInt(widthEt.getText().toString()), Integer.parseInt(heightEt.getText().toString()), yuvFlag)) {
                Log.e(TAG, "onCreate: prepare renderer失败");
                return;
            }
            if (checkFileState(FileUtil.prepare(pathTv.getText().toString()))) {
                int size = (Integer.parseInt(widthEt.getText().toString())) * (Integer.parseInt(heightEt.getText().toString()));
                ydata = new byte[size];
                udata = new byte[size / 4];
                vdata = new byte[size / 4];
                pos = 0;
                FileUtil.read(pos, ydata, udata, vdata);
                renderer.obtainYUVData(ydata, udata, vdata);
                frameData.requestRender();
            }
        });

        front.setOnClickListener(V -> {
            pos -= yuvSize();
            if (checkFileState(FileUtil.read(pos, ydata, udata, vdata))) {
                FileUtil.read(pos, ydata, udata, vdata);
                renderer.obtainYUVData(ydata, udata, vdata);
                frameData.requestRender();
            }
        });

        behind.setOnClickListener(v -> {
            pos += yuvSize();
            Log.e(TAG, "onCreate: " + pos);
            if (checkFileState(FileUtil.read(pos, ydata, udata, vdata))) {
                FileUtil.read(pos, ydata, udata, vdata);
                renderer.obtainYUVData(ydata, udata, vdata);
                frameData.requestRender();
            }
        });
    }


    private void initView() {
        chooseFileBtn = findViewById(R.id.yuv_choose_btn);
        parseBtn = findViewById(R.id.yuv_parse_btn);
        pathTv = findViewById(R.id.yuv_path_et);
        codecNameSpinner = findViewById(R.id.yuv_codec_name_spinner);
        showTypeSpinner = findViewById(R.id.yuv_show_type_spinner);
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
        renderer = new YUVRenderer(this);

        frameData.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        frameData.setEGLContextClientVersion(2);
        frameData.setRenderer(renderer);
        frameData.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        codecNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemSelected: " + getResources().getStringArray(R.array.codec_name)[position]);
                switch (getResources().getStringArray(R.array.codec_name)[position]) {
                    case "YUV420P":
                        yuvFlag = YUVUtil.YUV_TYPE.YUV_420P;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                yuvFlag = YUVUtil.YUV_TYPE.YUV_420P;
                Log.e(TAG, "onNothingSelected: ");
            }
        });

        showTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = getResources().getStringArray(R.array.show_type)[position];
                showFlag = 0;
                for (char c : s.toCharArray()) {
                    if (c == 'Y') {
                        showFlag |= YUVRenderer.SHOW_Y;
                    }
                    if (c == 'U') {
                        showFlag |= YUVRenderer.SHOW_U;
                    }
                    if (c == 'V') {
                        showFlag |= YUVRenderer.SHOW_V;
                    }
                }
                if (renderer != null) {
                    renderer.setShowFlag(showFlag);
                    frameData.requestRender();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e(TAG, "onNothingSelected: noThing select");
                renderer.setShowFlag(YUVRenderer.SHOW_Y | YUVRenderer.SHOW_U | YUVRenderer.SHOW_V);
            }
        });
    }

    private void chooseFile() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("file/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(i, "Select a File to Upload"), FILE_SELECT_CODE);
    }

    private int yuvSize() {
        return ydata.length + udata.length + vdata.length;
    }

    private boolean checkFileState(int fileState) {
        switch (fileState) {
            case FILE_STATE_PREPARED:
                Toast.makeText(this, "准备成功", Toast.LENGTH_SHORT).show();
                return true;
            case FILE_STATE_CLOSED:
                Toast.makeText(this, "已关闭", Toast.LENGTH_SHORT).show();
                return false;
            case FILE_STATE_UN_PREPARED:
                Toast.makeText(this, "准备失败", Toast.LENGTH_SHORT).show();
                return false;
            case FILE_STATE_UN_EXIST:
                Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
                return false;
            case FILE_STATE_END:
                Toast.makeText(this, "文件已读至末尾", Toast.LENGTH_SHORT).show();
                return false;
            case FILE_STATE_START:
                Toast.makeText(this, "文件以到达起点", Toast.LENGTH_SHORT).show();
                return false;
            case FILE_STATE_READING:
                Log.e(TAG, "checkFileState: 文件正常阅读");
                return true;
            default:
                Log.e(TAG, "checkFileState: 未定义状态");
                return false;
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