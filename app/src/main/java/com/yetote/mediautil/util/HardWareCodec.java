package com.yetote.mediautil.util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.NonNull;

import com.yetote.mediautil.bean.CodecInfoBean;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static android.media.AudioFormat.ENCODING_PCM_16BIT;
import static android.media.MediaCodec.BUFFER_FLAG_END_OF_STREAM;
import static android.media.MediaCodecInfo.CodecProfileLevel.AACObjectLC;

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
        FileUtils.checkState(FileUtils.checkFile(inputPath), FileUtils.FILE_STATE_SUCCESS);
        FileUtils.checkState(FileUtils.checkFile(outputPath), FileUtils.FILE_STATE_SUCCESS);

        try {
            FileInputStream rawStream = new FileInputStream(inputPath);
            FileChannel inputChannel = rawStream.getChannel();
            FileOutputStream encodecStream = new FileOutputStream(outputPath);
            FileChannel outputChannel = encodecStream.getChannel();
            int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelCount, ENCODING_PCM_16BIT);
            MediaCodec mediaCodec = MediaCodec.createByCodecName(codecName);
            Log.e(TAG, "encodeAudio: codecname" + mediaCodec.getName());
            MediaFormat mediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelCount);
            mediaFormat.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_AUDIO_AAC);
            mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, channelCount);
            mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate);
            mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, AACObjectLC);
            mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, minBufferSize * 2);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, sampleRate * channelCount);
            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            if (codecType == HW_ENCODEC_TYPE_ASYNCHRONOUS) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    codecByAync(mediaCodec, inputChannel, outputChannel, rawStream, encodecStream);
                    mediaCodec.start();
                } else {
                    return;
                }
            } else {
                codecBySync(mediaCodec);
                mediaCodec.start();
            }

        } catch (IOException e) {
            Log.e(TAG, "encodeAudio: " + e.toString());
        }
    }

    private static void codecBySync(MediaCodec mediaCodec) {

    }

    private static void codecByAync(MediaCodec mediaCodec, FileChannel inputChannel, FileChannel outputChannel, FileInputStream rawStream, FileOutputStream encodecStream) {
        Log.e(TAG, "codecBySync: ");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        pos = 0;
        HandlerThread handlerThread = new HandlerThread("encodeHandler");
        handlerThread.start();
        mediaCodec.setCallback(new MediaCodec.Callback() {
            @Override
            public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                Log.e(TAG, "onInputBufferAvailable: index" + index);
                ByteBuffer buffer = codec.getInputBuffer(index);
                buffer.clear();
                byte[] data = new byte[buffer.capacity()];
                int rst = FileUtils.read(inputChannel, pos, data);
                buffer.put(data);
                buffer.flip();
                Log.e(TAG, "onInputBufferAvailable: limit" + buffer.limit());
                if (rst == FileUtils.FILE_STATE_END) {
                    codec.queueInputBuffer(index, 0, 0, 0, BUFFER_FLAG_END_OF_STREAM);
                } else {
                    codec.queueInputBuffer(index, 0, data.length, 0, 0);
                }
                pos += data.length;
            }

            @Override
            public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                ByteBuffer buffer = codec.getOutputBuffer(index);
                Log.e(TAG, "onOutputBufferAvailable:flags " + info.flags);
                if (info.flags == BUFFER_FLAG_END_OF_STREAM) {
                    mediaCodec.stop();
                    mediaCodec.release();
                    try {
                        inputChannel.close();
                        outputChannel.close();
                        rawStream.close();
                        encodecStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e(TAG, "onOutputBufferAvailable: buffer limit" + buffer.limit());
                    byte[] bytes = new byte[buffer.limit()];
                    buffer.get(bytes);
                    FileUtils.write(outputChannel, MediaUtil.synthesisADTS(bytes.length, AACObjectLC, 44100, 2), bytes);
                    buffer.clear();
                    codec.releaseOutputBuffer(index, false);
                }
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
