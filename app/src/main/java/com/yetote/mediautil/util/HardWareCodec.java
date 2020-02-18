package com.yetote.mediautil.util;

import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import static android.media.AudioFormat.ENCODING_PCM_16BIT;

/**
 * 硬编码API
 */
public class HardWareCodec {
    private static final String TAG = "HardWareCodec";

    public static void encodeAudio(String codecName, String mime, int sampleRate, int channelCount) {
        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelCount, ENCODING_PCM_16BIT);
        try {
            MediaCodec mediaCodec = MediaCodec.createByCodecName(codecName);
            MediaFormat mediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelCount);
            mediaFormat.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_AUDIO_AAC);
            mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, channelCount);
            mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate);
            mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, minBufferSize * 2);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, sampleRate * channelCount);
            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mediaCodec.start();

//            mediaCodec.setCallback(new MediaCodec.Callback() {
//                @Override
//                public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
//
//                }
//
//                @Override
//                public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
//
//                }
//
//                @Override
//                public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
//
//                }
//
//                @Override
//                public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
//
//                }
//            },new Handler());


            mediaCodec.stop();
            mediaCodec.release();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
