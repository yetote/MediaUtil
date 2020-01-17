package com.yetote.mediautil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yetote.mediautil.bean.CodecInfoBean;
import com.yetote.mediautil.bean.HwBean;
import com.yetote.mediautil.util.DeviceUtil;

public class CodecInfoActivity extends AppCompatActivity {
    String codecName;
    private LinearLayout videoCodecInfo, audioCodecInfo, encodeInfo;
    private TextView codecInfoTitle, codecInfoMaxInstances, codecInfoMimeType, codecInfoDefaultMimeType, codecInfoVideoBitRate, codecInfoVideoWidthAlignment, codecInfoVideoHeightAlignment, codecInfoVideoSupportedFrameRates, codecInfoVideoSupportedWidths, codecInfoVideoSupportedHeights, codecInfoVideoSupportedPerformancePoints, codecInfoAudioBitRate, codecInfoAudioMaxInputChannelCount, codecInfoAudioSupportedSampleRateRange, codecInfoAudioSupportedSampleRates, codecInfoEncodeComplexity, codecInfoEncodeQuality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codec_info);

        Intent i = getIntent();
        codecName = i.getStringExtra("codecName");


        initView();
        CodecInfoBean codecInfoBean = DeviceUtil.checkCodec(codecName);

        if (codecInfoBean.getVideoCodecInfo() == null) {
            videoCodecInfo.setVisibility(View.GONE);
        }
        if (codecInfoBean.getAudioCodecInfo() == null) {
            audioCodecInfo.setVisibility(View.GONE);
        }
        if (codecInfoBean.getEncoderInfo() == null) {
            encodeInfo.setVisibility(View.GONE);
        }

        showText(codecInfoBean);
    }

    private void showText(CodecInfoBean codecInfoBean) {
        codecInfoMaxInstances.setText(String.format(getResources().getString(R.string.codec_info), "支持最大实例化数量", codecInfoBean.getMaxInstances() + ""));
        codecInfoMimeType.setText(String.format(getResources().getString(R.string.codec_info), "支持的mimeType", codecInfoBean.getMimeType()));
        codecInfoDefaultMimeType.setText(String.format(getResources().getString(R.string.codec_info), "编解码器设置的默认mimeType", codecInfoBean.getDefaultMimeType()));
        if (codecInfoBean.getVideoCodecInfo() != null) {
            codecInfoVideoBitRate.setText(String.format(getResources().getString(R.string.codec_info), "支持的比特率范围（bit/s）", codecInfoBean.getVideoCodecInfo().getBitRate()));
            codecInfoVideoWidthAlignment.setText(String.format(getResources().getString(R.string.codec_info), "视频高度的对齐要求", codecInfoBean.getVideoCodecInfo().getWidthAlignment() + ""));
            codecInfoVideoHeightAlignment.setText(String.format(getResources().getString(R.string.codec_info), "视频宽度的对齐要求", codecInfoBean.getVideoCodecInfo().getHeightAlignment() + ""));
            codecInfoVideoSupportedFrameRates.setText(String.format(getResources().getString(R.string.codec_info), "支持的帧率范围", codecInfoBean.getVideoCodecInfo().getSupportedFrameRates()));
            codecInfoVideoSupportedWidths.setText(String.format(getResources().getString(R.string.codec_info), "支持的视频宽度", codecInfoBean.getVideoCodecInfo().getSupportedWidths()));
            codecInfoVideoSupportedHeights.setText(String.format(getResources().getString(R.string.codec_info), "支持的视频高度", codecInfoBean.getVideoCodecInfo().getSupportedHeights()));
            codecInfoVideoSupportedPerformancePoints.setText(String.format(getResources().getString(R.string.codec_info), "支持的性能点", codecInfoBean.getVideoCodecInfo().getSupportedPerformancePoints()));
        }
        if (codecInfoBean.getAudioCodecInfo() != null) {
            codecInfoAudioBitRate.setText(String.format(getResources().getString(R.string.codec_info), "支持的比特率范围（bit/s）", codecInfoBean.getAudioCodecInfo().getBitRate()));
            codecInfoAudioMaxInputChannelCount.setText(String.format(getResources().getString(R.string.codec_info), "支持的最大声道数量", codecInfoBean.getAudioCodecInfo().getMaxInputChannelCount() + ""));
            codecInfoAudioSupportedSampleRateRange.setText(String.format(getResources().getString(R.string.codec_info), "支持的最大采样率范围", codecInfoBean.getAudioCodecInfo().getSupportedSampleRateRange()));
            codecInfoAudioSupportedSampleRates.setText(String.format(getResources().getString(R.string.codec_info), "支持的采样率", codecInfoBean.getAudioCodecInfo().getSupportedSampleRates()));
        }
        if (codecInfoBean.getEncoderInfo() != null) {
            codecInfoEncodeComplexity.setText(String.format(getResources().getString(R.string.codec_info), "解码器支持的复杂度范围", codecInfoBean.getEncoderInfo().getComplexity()));
            codecInfoEncodeQuality.setText(String.format(getResources().getString(R.string.codec_info), "支持的质量值范围", codecInfoBean.getEncoderInfo().getQuality()));
        }
    }

    private void initView() {
        videoCodecInfo = findViewById(R.id.codec_info_video);
        audioCodecInfo = findViewById(R.id.codec_info_audio);
        encodeInfo = findViewById(R.id.codec_info_encoder);
        codecInfoTitle = findViewById(R.id.codec_info_title);
        codecInfoMaxInstances = findViewById(R.id.codec_info_maxInstances);
        codecInfoMimeType = findViewById(R.id.codec_info_mimeType);
        codecInfoDefaultMimeType = findViewById(R.id.codec_info_defaultMimeType);
        codecInfoVideoBitRate = findViewById(R.id.codec_info_video_bitRate);
        codecInfoVideoWidthAlignment = findViewById(R.id.codec_info_video_widthAlignment);
        codecInfoVideoHeightAlignment = findViewById(R.id.codec_info_video_heightAlignment);
        codecInfoVideoSupportedFrameRates = findViewById(R.id.codec_info_video_supportedFrameRates);
        codecInfoVideoSupportedWidths = findViewById(R.id.codec_info_video_supportedWidths);
        codecInfoVideoSupportedHeights = findViewById(R.id.codec_info_video_supportedHeights);
        codecInfoVideoSupportedPerformancePoints = findViewById(R.id.codec_info_video_supportedPerformancePoints);
        codecInfoAudioBitRate = findViewById(R.id.codec_info_audio_bitRate);
        codecInfoAudioMaxInputChannelCount = findViewById(R.id.codec_info_audio_maxInputChannelCount);
        codecInfoAudioSupportedSampleRateRange = findViewById(R.id.codec_info_audio_supportedSampleRateRange);
        codecInfoAudioSupportedSampleRates = findViewById(R.id.codec_info_audio_supportedSampleRates);
        codecInfoEncodeComplexity = findViewById(R.id.codec_info_encode_complexity);
        codecInfoEncodeQuality = findViewById(R.id.codec_info_encode_quality);

    }
}