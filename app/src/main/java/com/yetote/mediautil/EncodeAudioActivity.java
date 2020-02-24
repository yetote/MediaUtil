package com.yetote.mediautil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.core.app.ActivityCompat;

import com.yetote.mediautil.bean.HwBean;
import com.yetote.mediautil.interfaces.EncodeProgressCallback;
import com.yetote.mediautil.util.AndroidFileUtil;
import com.yetote.mediautil.util.DeviceUtil;
import com.yetote.mediautil.util.EncodeUtils;
import com.yetote.mediautil.util.FileUtils;
import com.yetote.mediautil.util.HardWareCodec;

import java.util.ArrayList;
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
    private Button chooseFormatBtn, chooseCodecBtn, chooserLevelBtn;
    private Spinner channelSpinner, sampleSpinner, encodeModeSpinner;
    private RadioGroup writeADTSRg;
    private ImageView help;

    private String codecLevel = "AAC LC";
    private int channelCount, sampleRate;
    private boolean isWriteADTS = true, isHardware = true;

    private EncodeUtils encodeUtils;

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
        channelSpinner = findViewById(R.id.encode_audio_channelLayout_spinner);
        sampleSpinner = findViewById(R.id.encode_audio_sampleRate_spinner);
        chooserLevelBtn = findViewById(R.id.encode_audio_choose_level_btn);
        writeADTSRg = findViewById(R.id.encode_audio_write_adts_rg);
        help = findViewById(R.id.encode_audio_choose_level_help);
        encodeModeSpinner = findViewById(R.id.encode_audio_encode_mode_spinner);
        // TODO: 2020/2/18 测试使用路径，正式要记得删除
        pathTv.setText("/storage/emulated/0/441stereo.pcm");


        encodeUtils = new EncodeUtils();
    }

    private void click() {
        chooseFileBtn.setOnClickListener(this);
        parseBtn.setOnClickListener(this);
        chooseFormatBtn.setOnClickListener(this);
        chooseCodecBtn.setOnClickListener(this);
        chooserLevelBtn.setOnClickListener(this);
        parseBtn.setOnClickListener(this);

        writeADTSRg.setOnCheckedChangeListener((group, checkedId) -> {
            isWriteADTS = checkedId != R.id.encode_audio_write_adts_false;
        });

        channelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                channelCount = getResources().getStringArray(R.array.channel_layout_arr)[position].equalsIgnoreCase("单声道") ? 1 : 2;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                channelCount = 1;
            }
        });

        sampleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sampleRate = Integer.parseInt(getResources().getStringArray(R.array.sample_rate_arr)[position]);
                Log.e(TAG, "onItemSelected: " + sampleRate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sampleRate = 8000;
            }
        });
        encodeModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isHardware = getResources().getStringArray(R.array.codec_mode_arr)[position].equalsIgnoreCase("硬解");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                isHardware = true;
            }
        });
    }

    private void chooseFile() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("file/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(i, "Select a File to Upload"), FILE_SELECT_CODE);
    }


    void showPopWindow(View view, ArrayList<String> list) {
        ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setWidth(measureContentWidth(adapter));//也可以设置具体的值。容易出错的地方
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        listPopupWindow.setAnchorView(view);//设置参照控件
        listPopupWindow.setDropDownGravity(Gravity.START);//设置对齐方式。Gravity.START表示与参照控件左侧对齐，Gravity.END表示与参照控件右侧对齐。容易出错的地方
        listPopupWindow.setModal(true);//模态框，设置为true响应物理键
        listPopupWindow.show();
        ListView listView = listPopupWindow.getListView();
        if (listView != null) {
            listView.setOnItemClickListener((parent, view1, position, id) -> {
                ((Button) view).setText(list.get(position));
                listPopupWindow.dismiss();
            });

            listView.setOnItemLongClickListener((parent, view12, position, id) -> {
                Intent i = new Intent(EncodeAudioActivity.this, CodecInfoActivity.class);
                i.putExtra("codecName", list.get(position));
                startActivity(i);
                return true;
            });
        }
    }


    public void clear() {

    }

    private int measureContentWidth(ListAdapter listAdapter) {
        ViewGroup mMeasureParent = null;
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int count = listAdapter.getCount();
        for (int i = 0; i < count; i++) {
            int positionType = listAdapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(this);
            }

            itemView = listAdapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemWidth = itemView.getMeasuredWidth();

            if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }

        return maxWidth;
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
//                chooseCodecBtn.setText(R.string.pause_to_choose_encodec_format);
                List<HwBean> list = DeviceUtil.checkAllCodec("audio");
                Set<String> mediaTypeSet = new HashSet<>();
                for (int i = 0; i < list.size(); i++) {
                    mediaTypeSet.add(list.get(i).getSupportedTypes().replace("audio/", ""));
                }
                showPopWindow(chooseFormatBtn, new ArrayList<>(mediaTypeSet));
                break;
            case R.id.encode_audio_choose_codec_btn:
                List<HwBean> list2 = DeviceUtil.checkAllCodec("audio/" + chooseFormatBtn.getText().toString(), DeviceUtil.CODEC_TYPE_ENCODER);
                List<String> codecNameList = new ArrayList<>();
                for (int i = 0; i < list2.size(); i++) {
                    codecNameList.add(list2.get(i).getCodecName());
                }
                Log.e(TAG, "onClick: " + Arrays.toString(codecNameList.toArray()));
                if (codecNameList.isEmpty()) {
                    Toast.makeText(this, "不存在对应的编解码器，请切换编解码方式", Toast.LENGTH_SHORT).show();
                } else {
                    showPopWindow(chooseCodecBtn, new ArrayList<>(codecNameList));
                }
                break;

            case R.id.encode_audio_parse_btn:
                if (pathTv.getText().toString().isEmpty() || !pathTv.getText().toString().endsWith(".pcm")) {
                    Toast.makeText(this, "选择的文件格式不正确，必须为pcm原始数据", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (chooseFormatBtn.getText().toString().equalsIgnoreCase(getString(R.string.pause_to_choose_encodec_format))) {
                    Toast.makeText(this, "请选择编码格式", Toast.LENGTH_SHORT).show();
                    return;
                }

                int rst;
                if (chooseCodecBtn.getText().toString().isEmpty()) {
                    rst = DeviceUtil.checkAudioEncoderParams(chooseCodecBtn.getText().toString(), channelCount, sampleRate);
                } else {
                    rst = DeviceUtil.CHECK_ENCODER_SUCCESS;
                }

                switch (rst) {
                    case DeviceUtil.CHECK_ENCODER_SUCCESS:
                        String codecName = chooseCodecBtn.getText().toString().equalsIgnoreCase(getString(R.string.pause_to_choose_encodec)) ? "default" : chooseCodecBtn.getText().toString();
                        String mime = chooseFormatBtn.getText().toString();
//                        String encodeType = HardWareCodec.HW_ENCODEC_TYPE_ASYNCHRONOUS==;
                        codecLevel = chooserLevelBtn.getText().toString().equalsIgnoreCase(getString(R.string.pause_to_choose_level)) ? "AAC LC" : chooserLevelBtn.getText().toString();
                        String outputPath = this.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getPath() + "/" + codecName.replace(".", "_") + "_" + codecLevel.replace(" ", "_") + ".aac";
                        if (FileUtils.createFile(outputPath)) {
//                            HardWareCodec.encodeAudio(pathTv.getText().toString(), outputPath, codecName, "audio/" + mime, sampleRate, channelCount, HardWareCodec.HW_ENCODEC_TYPE_ASYNCHRONOUS, codecLevel, isWriteADTS, progressCallback);
                            encodeUtils.setInputPath(pathTv.getText().toString())
                                    .setOutputPath(outputPath)
                                    .setHardware(isHardware)
                                    .setProgressCallback(progress -> {
                                        Log.e(TAG, "setProgress: " + progress);
                                        if (progress == 100) {
                                            Toast.makeText(this, "编码完成", Toast.LENGTH_SHORT).show();
                                            encodeUtils.destroy();
                                        }
                                    }).encodeAudio("audio/" + mime, channelCount, sampleRate, codecName, codecLevel, true);
                        } else {
                            Toast.makeText(this, "无法创建文件，请检查权限", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case DeviceUtil.CHECK_ENCODER_UNKNOWN_ERR:
                        Toast.makeText(this, "编码器检查失败，未知错误", Toast.LENGTH_SHORT).show();
                        break;
                    case DeviceUtil.CHECK_ENCODER_CHANNEL_ERR:
                        Toast.makeText(this, "编码器检查失败，声道数超出范围", Toast.LENGTH_SHORT).show();
                        break;
                    case DeviceUtil.CHECK_ENCODER_SAMPLERATE_ERR:
                        Toast.makeText(this, "编码器检查失败，采样率超出范围", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
            case R.id.encode_audio_choose_level_btn:
                List<String> levelList = Arrays.asList(getResources().getStringArray(R.array.aac_level_arr));
                showPopWindow(chooserLevelBtn, new ArrayList<>(levelList));
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
        clear();
        super.onDestroy();
    }

}