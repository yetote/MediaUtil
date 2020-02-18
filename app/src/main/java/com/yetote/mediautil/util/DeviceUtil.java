package com.yetote.mediautil.util;

import android.annotation.SuppressLint;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.util.Log;

import com.yetote.mediautil.bean.CodecInfoBean;
import com.yetote.mediautil.bean.HwBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.media.MediaCodecList.ALL_CODECS;

public class DeviceUtil {
    public static final int CODEC_TYPE_DECODER = 0X0000;
    public static final int CODEC_TYPE_ENCODER = 0X0001;

    public static final int CHECK_ENCODER_SUCCESS = 0X0002;
    public static final int CHECK_ENCODER_UNKNOWN_ERR = 0X0003;
    public static final int CHECK_ENCODER_CHANNEL_ERR = 0X0004;
    public static final int CHECK_ENCODER_SAMPLERATE_ERR = 0X0005;

    public static HwBean[] checkAllCodec() {
        MediaCodecList mediaCodecList = new MediaCodecList(ALL_CODECS);
        MediaCodecInfo[] mediaCodecInfos = mediaCodecList.getCodecInfos();
        HwBean[] hwBeans = new HwBean[mediaCodecInfos.length];
        for (int i = 0; i < mediaCodecInfos.length; i++) {
            hwBeans[i] = obtainMediaCodecInfo(mediaCodecInfos[i], "", -1);
        }
        return hwBeans;
    }

    public static ArrayList<HwBean> checkAllCodec(String mediaType) {
        MediaCodecList mediaCodecList = new MediaCodecList(ALL_CODECS);
        MediaCodecInfo[] mediaCodecInfos = mediaCodecList.getCodecInfos();
        ArrayList<HwBean> hwBeansList = new ArrayList<>();
        for (MediaCodecInfo mediaCodecInfo : mediaCodecInfos) {
            HwBean hwBean = obtainMediaCodecInfo(mediaCodecInfo, mediaType, -1);
            if (hwBean != null) {
                hwBeansList.add(hwBean);
            }
        }
        return hwBeansList;
    }

    public static ArrayList<HwBean> checkAllCodec(String mediaType, int codecType) {
        if (codecType != CODEC_TYPE_DECODER && codecType != CODEC_TYPE_ENCODER) {
            throw new IllegalArgumentException("codecType 仅允许 CODEC_TYPE_DECODER 和 CODEC_TYPE_ENCODER ");
        }
        MediaCodecList mediaCodecList = new MediaCodecList(ALL_CODECS);
        MediaCodecInfo[] mediaCodecInfos = mediaCodecList.getCodecInfos();
        ArrayList<HwBean> hwBeansList = new ArrayList<>();
        for (MediaCodecInfo mediaCodecInfo : mediaCodecInfos) {
            HwBean hwBean = obtainMediaCodecInfo(mediaCodecInfo, mediaType, codecType);
            if (hwBean != null) {
                hwBeansList.add(hwBean);
            }
        }
        return hwBeansList;

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

    public static int checkAudioEncoderParams(String codecName, int channelCount, int sampleRate) {
        MediaCodecList mediaCodecList = new MediaCodecList(ALL_CODECS);
        MediaCodecInfo[] mediaCodecInfos = mediaCodecList.getCodecInfos();
        int[] sampleRateArr;
        int maxChannelCount;
        for (MediaCodecInfo m : mediaCodecInfos) {
            if (m.getName().equalsIgnoreCase(codecName)) {
                MediaCodecInfo.CodecCapabilities codecCapabilities = m.getCapabilitiesForType(m.getSupportedTypes()[0]);
                sampleRateArr = codecCapabilities.getAudioCapabilities().getSupportedSampleRates();
                maxChannelCount = codecCapabilities.getAudioCapabilities().getMaxInputChannelCount();
                if (channelCount > maxChannelCount) {
                    return CHECK_ENCODER_CHANNEL_ERR;
                }

                for (int value : sampleRateArr) {
                    if (sampleRate == value) {
                        return CHECK_ENCODER_SUCCESS;
                    }
                }
                return CHECK_ENCODER_SAMPLERATE_ERR;
            }
        }
        return CHECK_ENCODER_UNKNOWN_ERR;
    }

    /**
     * 获取解码器信息
     *
     * @param mediaCodecInfo MediaCodecInfo实体类
     * @param mediaType      MIME
     * @param codecType      是否为编码器，1为true，0为false，二者之外为全部编解码器
     * @return 解码器信息实例
     */
    private static HwBean obtainMediaCodecInfo(MediaCodecInfo mediaCodecInfo, String mediaType, int codecType) {
        String canonicalName;
        String isSoftwareOnly;
        String isAlias;
        String isHardwareAccelerated;
        String isVendor;
        if (!mediaCodecInfo.getSupportedTypes()[0].startsWith(mediaType)) {
            return null;
        }
        if (codecType == 0) {
            //解码器
            if (mediaCodecInfo.isEncoder()) {
                return null;
            }
        } else if (codecType == 1) {
            //编码器
            if (!mediaCodecInfo.isEncoder()) {
                return null;
            }
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            canonicalName = mediaCodecInfo.getCanonicalName();
            isSoftwareOnly = (mediaCodecInfo.isSoftwareOnly()) ? "是" : "否";
            isAlias = (mediaCodecInfo.isAlias()) ? "是" : "否";
            isHardwareAccelerated = (mediaCodecInfo.isHardwareAccelerated()) ? "支持" : "不支持";
            isVendor = (mediaCodecInfo.isVendor()) ? "是" : "否";
        } else {
            canonicalName = isSoftwareOnly = isAlias = isHardwareAccelerated = isVendor = "无法获取，需要api>=29";
        }
        String supportedTypes = mediaCodecInfo.getSupportedTypes()[0];
        String codecName = mediaCodecInfo.getName();
        String isEncoder = (mediaCodecInfo.isEncoder()) ? "是" : "否";

        return new HwBean(codecName, canonicalName, supportedTypes, isAlias, isEncoder, isHardwareAccelerated, isSoftwareOnly, isVendor);
    }

    /**
     * 获取解码器详细信息
     *
     * @param capabilities MediaCodecInfo.CodecCapabilities
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
            StringBuilder supportedPerformancePointStr = new StringBuilder();
            if (supportedPerformancePoints != null) {
                for (int i = 0; i < supportedPerformancePoints.size(); i++) {
                    supportedPerformancePointStr.append(supportedPerformancePoints.get(i).toString()).append("fps\n");
                }
            }
            codecInfoBean.getVideoCodecInfo().setSupportedPerformancePoints(supportedPerformancePointStr.toString());
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
