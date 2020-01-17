package com.yetote.mediautil.util;

import android.annotation.SuppressLint;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.util.Log;
import android.util.Range;

import com.yetote.mediautil.bean.CodecInfoBean;
import com.yetote.mediautil.bean.HwBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.media.MediaCodecList.ALL_CODECS;

public class DeviceUtil {
    private static final String TAG = "DeviceUtil";

    public static HwBean[] checkAllCodec() {
        MediaCodecList mediaCodecList = new MediaCodecList(ALL_CODECS);
        MediaCodecInfo[] mediaCodecInfos = mediaCodecList.getCodecInfos();
        HwBean[] hwBeans = new HwBean[mediaCodecInfos.length];
        for (int i = 0; i < mediaCodecInfos.length; i++) {
            hwBeans[i] = obtainMediaCodecInfo(mediaCodecInfos[i]);
        }
        return hwBeans;
    }

    public static CodecInfoBean checkCodec(String codecName) {
        MediaCodecList mediaCodecList = new MediaCodecList(ALL_CODECS);
        MediaCodecInfo[] mediaCodecInfos = mediaCodecList.getCodecInfos();
        for (MediaCodecInfo m : mediaCodecInfos) {
            if (m.getName().equalsIgnoreCase(codecName)) {
                MediaCodecInfo.CodecCapabilities codecCapabilities = m.getCapabilitiesForType(m.getSupportedTypes()[0]);
                return obtainDetailedCodecInfo(codecCapabilities);
            }
        }
        return null;
    }

    private static HwBean obtainMediaCodecInfo(MediaCodecInfo mediaCodecInfo) {
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

        return new HwBean(codecName, canonicalName, supportedTypes, isAlias, isEncoder, isHardwareAccelerated, isSoftwareOnly, isVendor);
    }

    /**
     * 获取解码器详细信息
     *
     * @param capabilities
     */
    @SuppressLint("NewApi")
    private static CodecInfoBean obtainDetailedCodecInfo(MediaCodecInfo.CodecCapabilities capabilities) {
        CodecInfoBean codecInfoBean = new CodecInfoBean();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            codecInfoBean.setMaxInstances(capabilities.getMaxSupportedInstances());
        }

        codecInfoBean.setMimeType(capabilities.getMimeType());
        codecInfoBean.setDefaultMimeType(capabilities.getDefaultFormat().toString());

        MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();

        if (videoCapabilities != null) {
            codecInfoBean.setVideoCodecInfo(codecInfoBean.new VideoCodecInfo(
                    videoCapabilities.getBitrateRange().toString(),
                    videoCapabilities.getWidthAlignment(),
                    videoCapabilities.getHeightAlignment(),
                    videoCapabilities.getSupportedFrameRates().toString(),
                    videoCapabilities.getSupportedWidths().toString(),
                    videoCapabilities.getSupportedHeights().toString()
            ));
            List<MediaCodecInfo.VideoCapabilities.PerformancePoint> supportedPerformancePoints = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ? videoCapabilities.getSupportedPerformancePoints() : null;
            String supportedPerformancePointStr = "";
            if (supportedPerformancePoints != null) {
                for (int i = 0; i < supportedPerformancePoints.size(); i++) {
                    supportedPerformancePointStr += supportedPerformancePoints.get(i).toString() + "fps\n";
                }
            }
            codecInfoBean.getVideoCodecInfo().setSupportedPerformancePoints(supportedPerformancePointStr);
        } else {
            codecInfoBean.setVideoCodecInfo(null);
        }

        MediaCodecInfo.AudioCapabilities audioCapabilities = capabilities.getAudioCapabilities();
        if (audioCapabilities != null) {
            codecInfoBean.setAudioCodecInfo(codecInfoBean.new AudioCodecInfo(
                    audioCapabilities.getBitrateRange().toString(),
                    audioCapabilities.getMaxInputChannelCount(),
                    Arrays.toString(audioCapabilities.getSupportedSampleRateRanges()),
                    Arrays.toString(audioCapabilities.getSupportedSampleRates())
            ));
        } else {
            codecInfoBean.setAudioCodecInfo(null);
        }

        MediaCodecInfo.EncoderCapabilities encoderCapabilities = capabilities.getEncoderCapabilities();
        if (encoderCapabilities != null) {
            codecInfoBean.setEncoderInfo(codecInfoBean.new EncoderInfo(
                    encoderCapabilities.getComplexityRange().toString(),
                    ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ? encoderCapabilities.getQualityRange().toString() : null)
            ));
        } else {
            codecInfoBean.setEncoderInfo(null);
        }
        return codecInfoBean;
    }
}
