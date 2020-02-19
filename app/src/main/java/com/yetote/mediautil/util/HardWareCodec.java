package com.yetote.mediautil.util;

import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static android.media.AudioFormat.ENCODING_PCM_16BIT;

/**
 * 硬编码API
 */
public class HardWareCodec {
    private static final String TAG = "HardWareCodec";

    public static final int HW_ERR_SDK_NEED_M = 0x0002;
    public static final int HW_ENCODEC_TYPE_SYNCHRONOUS = 0X1000;
    public static final int HW_ENCODEC_TYPE_ASYNCHRONOUS = 0X1001;
    private static int pos = 0;

    public static void encodeAudio(String inputPath, String outputPath, String codecName, String mime, int sampleRate, int channelCount, int codecType) {
        if (codecType != HW_ENCODEC_TYPE_SYNCHRONOUS && codecType != HW_ENCODEC_TYPE_ASYNCHRONOUS) {
            throw new IllegalArgumentException("codecType 仅被允许为 HW_ENCODEC_TYPE_SYNCHRONOUS 或 HW_ENCODEC_TYPE_ASYNCHRONOUS ");
        }

        try {
            FileUtils.checkState(FileUtils.checkFile(inputPath), FileUtils.FILE_STATE_SUCCESS);
            FileUtils.checkState(FileUtils.checkFile(outputPath), FileUtils.FILE_STATE_SUCCESS);
            FileInputStream rawStream = new FileInputStream(inputPath);
            FileChannel inputChannel = rawStream.getChannel();
            FileInputStream encodecStream = new FileInputStream(outputPath);
            FileChannel outputChannel = encodecStream.getChannel();
            int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelCount, ENCODING_PCM_16BIT);
            MediaCodec mediaCodec = MediaCodec.createByCodecName(codecName);
            MediaFormat mediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelCount);
            mediaFormat.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_AUDIO_AAC);
            mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, channelCount);
            mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate);
            mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, minBufferSize * 2);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, sampleRate * channelCount);
            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            if (codecType == HW_ENCODEC_TYPE_ASYNCHRONOUS) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    codecByAync(mediaCodec, inputChannel, outputChannel);
                    mediaCodec.start();
                } else {
                    return;
                }
            } else {
                codecBySync(mediaCodec);
                mediaCodec.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void codecBySync(MediaCodec mediaCodec) {

    }

    private static void codecByAync(MediaCodec mediaCodec, FileChannel inputChannel, FileChannel outputChannel) {
        Log.e(TAG, "codecBySync: ");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        pos = 0;
        HandlerThread handlerThread = new HandlerThread("encodeHandler");
        handlerThread.start();
        mediaCodec.setCallback(new MediaCodec.Callback() {
            @Override
            public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                ByteBuffer buffer = codec.getInputBuffer(index);
                buffer.clear();
                Log.e(TAG, "onInputBufferAvailable: " + buffer.capacity());
                byte[] data = new byte[buffer.capacity()];
                buffer.put(data);
                int rst = FileUtils.read(inputChannel, pos, data);
                if (rst == FileUtils.FILE_STATE_END) {
                    codec.queueInputBuffer(index, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                } else {
                    codec.queueInputBuffer(index, 0, data.length, 0, MediaCodec.BUFFER_FLAG_KEY_FRAME);
                }
                pos += data.length;
            }

            @Override
            public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                ByteBuffer buffer = codec.getOutputBuffer(index);
                Log.e(TAG, "onOutputBufferAvailable:flags " + info.flags);
                buffer.clear();
                codec.releaseOutputBuffer(index, false);
            }

            @Override
            public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
                Log.e(TAG, "onError: " + e.toString());
            }

            @Override
            public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

            }
        }, new Handler(handlerThread.getLooper()));
    }
}
