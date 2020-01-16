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

import com.yetote.mediautil.bean.HwBean;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import static android.media.MediaCodecList.ALL_CODECS;

public class DeviceUtil {
    private static final String TAG = "DeviceUtil";

    public static HwBean[] checkCodec() {
        MediaCodecList mediaCodecList = new MediaCodecList(ALL_CODECS);
        MediaCodecInfo[] mediaCodecInfos = mediaCodecList.getCodecInfos();
        HwBean[] hwBeans = new HwBean[mediaCodecInfos.length];
        for (int i = 0; i < mediaCodecInfos.length; i++) {
//            Log.e(TAG, "selectCodec: " + caseMediaCodecInfo(mediaCodecInfos[i]));
            hwBeans[i] = caseMediaCodecInfo(mediaCodecInfos[i]);
        }
        return hwBeans;
    }

    public static HwBean caseMediaCodecInfo(MediaCodecInfo mediaCodecInfo) {

//        MediaCodecInfo.CodecCapabilities codecCapabilities = mediaCodecInfo.getCapabilitiesForType(mediaCodecInfo.getSupportedTypes()[0]);
//        Log.e(TAG, "mediaCodecInfoToString: getMimeType " + codecCapabilities.getMimeType());
//        Log.e(TAG, "mediaCodecInfoToString: getAudioCapabilities" + codecCapabilities.getAudioCapabilities());
//        Log.e(TAG, "mediaCodecInfoToString: getDefaultFormat" + codecCapabilities.getDefaultFormat());
//        Log.e(TAG, "mediaCodecInfoToString: getEncoderCapabilities" + codecCapabilities.getEncoderCapabilities());
//        Log.e(TAG, "mediaCodecInfoToString: getMaxSupportedInstances" + codecCapabilities.getMaxSupportedInstances());
//        Log.e(TAG, "mediaCodecInfoToString: getVideoCapabilities" + codecCapabilities.getVideoCapabilities());
        String canonicalName;
        String isSoftwareOnly;
        String isAlias;
        String isHardwareAccelerated;
        String isVendor;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            canonicalName = mediaCodecInfo.getCanonicalName();
            isSoftwareOnly = (mediaCodecInfo.isSoftwareOnly()) ? "是" : "否";
            isAlias = (mediaCodecInfo.isAlias()) ? "是" : "否";
            isHardwareAccelerated = (mediaCodecInfo.isHardwareAccelerated()) ? "支持" : "不支持";
            isVendor = (mediaCodecInfo.isVendor()) ? "是" : "否";
        } else {
            canonicalName = isSoftwareOnly = isAlias = isHardwareAccelerated = isVendor = "无法获取，需要api>=29";
        }
        String codecName = mediaCodecInfo.getName();
        String supportedTypes = Arrays.toString(mediaCodecInfo.getSupportedTypes());
        String isEncoder = (mediaCodecInfo.isEncoder()) ? "是" : "否";

        HwBean hwBean = new HwBean(codecName, canonicalName, supportedTypes, isAlias, isEncoder, isHardwareAccelerated, isSoftwareOnly, isVendor);


        return hwBean;


//        return "编解码器名称:" + mediaCodecInfo.getName() + "\n"
//                + "检索基础编解码器名称:" + mediaCodecInfo.getCanonicalName() + "\n"
//                + "枚举编解码器组件的功能:" + mediaCodecInfo.getCapabilitiesForType(mediaCodecInfo.getSupportedTypes()[0]) + "\n"
//                + "支持的媒体类型:" + Arrays.toString(mediaCodecInfo.getSupportedTypes()) + "\n"
//                + "是否为别名:" + mediaCodecInfo.isAlias() + "\n"
//                + "是否为编码器:" + mediaCodecInfo.isEncoder() + "\n"
//                + "是否使用硬件加速:" + mediaCodecInfo.isHardwareAccelerated() + "\n"
//                + "是否为软编解码器:" + mediaCodecInfo.isSoftwareOnly() + "\n"
//                + "是否由供应商提供 : " + mediaCodecInfo.isVendor() + "\n";
    }
}
