package com.yetote.mediautil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yetote.mediautil.adapter.PopupWindowRVAdapter;
import com.yetote.mediautil.bean.HwBean;
import com.yetote.mediautil.interfaces.OnClick;
import com.yetote.mediautil.util.AndroidFileUtil;
import com.yetote.mediautil.util.DeviceUtil;
import com.yetote.mediautil.util.FileUtil;
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
    private Button chooseFormatBtn, chooseCodecBtn;
    private PopupWindowRVAdapter adapter;

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
        chooseCodecBtn.setOnClickListener(this);
    }

    private void chooseFile() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("file/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(i, "Select a File to Upload"), FILE_SELECT_CODE);
    }


    void showPopWindow(View view, ArrayList<String> list) {
        ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setWidth(measureContentWidth(adapter));//也可以设置具体的值。容易出错的地方
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        listPopupWindow.setAnchorView(view);//设置参照控件
        listPopupWindow.setDropDownGravity(Gravity.START);//设置对齐方式。Gravity.START表示与参照控件左侧对齐，Gravity.END表示与参照控件右侧对齐。容易出错的地方
        listPopupWindow.setModal(true);//模态框，设置为true响应物理键
        listPopupWindow.show();
        ListView listView = listPopupWindow.getListView();
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            ((Button) view).setText(list.get(position));
            listPopupWindow.dismiss();
        });
    }


    public void clear() {

    }

    private int measureContentWidth(ListAdapter listAdapter) {
        ViewGroup mMeasureParent = null;
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        final ListAdapter adapter = listAdapter;
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(this);
            }

            itemView = adapter.getView(i, itemView, mMeasureParent);
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