package com.yetote.mediautil.util;

import android.content.ContentResolver;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import static android.media.MediaCodecList.ALL_CODECS;

public class DeviceUtil {
    private static final String TAG = "DeviceUtil";

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void checkCodec(int mimeType) {
        MediaCodecList mediaCodecList = new MediaCodecList(ALL_CODECS);
        MediaCodecInfo[] mediaCodecInfos = mediaCodecList.getCodecInfos();
        for (int i = 0; i < mediaCodecInfos.length; i++) {
            Log.e(TAG, "selectCodec: " + mediaCodecInfoToString(mediaCodecInfos[i]));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static String mediaCodecInfoToString(MediaCodecInfo mediaCodecInfo) {
        return "编解码器名称:" + mediaCodecInfo.getName() + "\n"
                + "检索基础编解码器名称:" + mediaCodecInfo.getCanonicalName() + "\n"
                + "枚举编解码器组件的功能:" + mediaCodecInfo.getCapabilitiesForType(mediaCodecInfo.getSupportedTypes()[0]) + "\n"
                + "支持的媒体类型:" + Arrays.toString(mediaCodecInfo.getSupportedTypes()) + "\n"
                + "是否为别名:" + mediaCodecInfo.isAlias() + "\n"
                + "是否为编码器:" + mediaCodecInfo.isEncoder() + "\n"
                + "是否使用硬件加速:" + mediaCodecInfo.isHardwareAccelerated() + "\n"
                + "是否为软:" + mediaCodecInfo.isSoftwareOnly() + "\n"
                + "是否由供应商提供 : " + mediaCodecInfo.isVendor() + "\n";
    }
}
