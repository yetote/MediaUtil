package com.yetote.mediautil.util;

import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.NonNull;

import com.yetote.mediautil.interfaces.EncodeProgressCallback;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

import static android.media.AudioFormat.ENCODING_PCM_16BIT;
import static android.media.MediaCodec.BUFFER_FLAG_END_OF_STREAM;
import static android.media.MediaCodecInfo.CodecProfileLevel.AACObjectELD;
import static android.media.MediaCodecInfo.CodecProfileLevel.AACObjectERLC;
import static android.media.MediaCodecInfo.CodecProfileLevel.AACObjectERScalable;
import static android.media.MediaCodecInfo.CodecProfileLevel.AACObjectHE;
import static android.media.MediaCodecInfo.CodecProfileLevel.AACObjectHE_PS;
import static android.media.MediaCodecInfo.CodecProfileLevel.AACObjectLC;
import static android.media.MediaCodecInfo.CodecProfileLevel.AACObjectLD;
import static android.media.MediaCodecInfo.CodecProfileLevel.AACObjectLTP;
import static android.media.MediaCodecInfo.CodecProfileLevel.AACObjectMain;
import static android.media.MediaCodecInfo.CodecProfileLevel.AACObjectSSR;
import static android.media.MediaCodecInfo.CodecProfileLevel.AACObjectScalable;
import static android.media.MediaCodecInfo.CodecProfileLevel.AACObjectXHE;

/**
 * 硬编码API
 */
public class HardWareCodec {
    private static final String TAG = "HardWareCodec";

    public static final int HW_ERR_SDK_NEED_M = 0x0002;

    private static int pos = 0;

    private static HashMap<String, String> mimeMap = new HashMap<>();
    private static HashMap<String, Integer> aacObjectLevel = new HashMap<>();
    static int lastProgress = 0;

    static {
        mimeMap.put("audio/mp4a-latm", "aac");
        aacObjectLevel.put("AAC MAIN", AACObjectMain);
        aacObjectLevel.put("AAC LC", AACObjectLC);
        aacObjectLevel.put("AAC SSR", AACObjectSSR);
        aacObjectLevel.put("AAC LTP", AACObjectLTP);
        aacObjectLevel.put("AAC HE", AACObjectHE);
        aacObjectLevel.put("AAC Scalable", AACObjectScalable);
        aacObjectLevel.put("ER AAC LC", AACObjectERLC);
        aacObjectLevel.put("ER AAC Scalable", AACObjectERScalable);
        aacObjectLevel.put("ER AAC LD", AACObjectLD);
        aacObjectLevel.put("AAC HEv2", AACObjectHE_PS);
        aacObjectLevel.put("AAC ELD", AACObjectELD);
        aacObjectLevel.put("AAC xHE", AACObjectXHE);
//        MIMETYPE_VIDEO_VP8 = "video/x-vnd.on2.vp8";
//        MIMETYPE_VIDEO_VP9 = "video/x-vnd.on2.vp9";
//        MIMETYPE_VIDEO_AV1 = "video/av01";
//        MIMETYPE_VIDEO_AVC = "video/avc";
//        MIMETYPE_VIDEO_HEVC = "video/hevc";
//        MIMETYPE_VIDEO_MPEG4 = "video/mp4v-es";
//        MIMETYPE_VIDEO_H263 = "video/3gpp";
//        MIMETYPE_VIDEO_MPEG2 = "video/mpeg2";
//        MIMETYPE_VIDEO_RAW = "video/raw";
//        MIMETYPE_VIDEO_DOLBY_VISION = "video/dolby-vision";
//        MIMETYPE_VIDEO_SCRAMBLED = "video/scrambled";
//
//        MIMETYPE_AUDIO_AMR_NB = "audio/3gpp";
//        MIMETYPE_AUDIO_AMR_WB = "audio/amr-wb";
//        MIMETYPE_AUDIO_MPEG = "audio/mpeg";
//        MIMETYPE_AUDIO_AAC = "audio/mp4a-latm";
//        MIMETYPE_AUDIO_QCELP = "audio/qcelp";
//        MIMETYPE_AUDIO_VORBIS = "audio/vorbis";
//        MIMETYPE_AUDIO_OPUS = "audio/opus";
//        MIMETYPE_AUDIO_G711_ALAW = "audio/g711-alaw";
//        MIMETYPE_AUDIO_G711_MLAW = "audio/g711-mlaw";
//        MIMETYPE_AUDIO_RAW = "audio/raw";
//        MIMETYPE_AUDIO_FLAC = "audio/flac";
//        MIMETYPE_AUDIO_MSGSM = "audio/gsm";
//        MIMETYPE_AUDIO_AC3 = "audio/ac3";
//        MIMETYPE_AUDIO_EAC3 = "audio/eac3";
//        MIMETYPE_AUDIO_EAC3_JOC = "audio/eac3-joc";
//        MIMETYPE_AUDIO_AC4 = "audio/ac4";
//        MIMETYPE_AUDIO_SCRAMBLED = "audio/scrambled";
    }

    public static void encodeAudio(FileChannel inputChannel, FileChannel outputChannel, String codecName, String mime, int sampleRate, int channelCount, boolean codecType, String codecLevel, boolean isWriteADTS, EncodeProgressCallback progressCallback) {

        try {
            int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelCount, ENCODING_PCM_16BIT);
            MediaCodec mediaCodec;
            if (!codecName.equalsIgnoreCase("default")) {
                mediaCodec = MediaCodec.createByCodecName(codecName);
            } else {
                mediaCodec = MediaCodec.createEncoderByType(mime);
            }
            Log.e(TAG, "encodeAudio: codecname" + mediaCodec.getName());
            MediaFormat mediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelCount);
            mediaFormat.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_AUDIO_AAC);
            mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, channelCount);
            mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate);
            mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, aacObjectLevel.get(codecLevel));
            mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, minBufferSize * 2);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, sampleRate * channelCount / 4);
            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            if (codecType) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    codecByAync(mediaCodec, inputChannel, outputChannel, isWriteADTS, aacObjectLevel.get(codecLevel), progressCallback);
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

    private static void codecByAync(MediaCodec mediaCodec, FileChannel inputChannel, FileChannel outputChannel, boolean isWriteADTS, int codecLevel, EncodeProgressCallback progressCallback) {
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
                    try {
                        int progress = (int) ((float) rst / (float) inputChannel.size() * 100);
                        if (progress != 100 && progress - lastProgress >= 1) {
                            progressCallback.setProgress(progress);
                            lastProgress = progress;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                    progressCallback.setProgress(100);
                    lastProgress = 0;
                } else {
                    Log.e(TAG, "onOutputBufferAvailable: buffer limit" + buffer.limit());
                    byte[] bytes = new byte[buffer.limit()];
                    buffer.get(bytes);
                    if (isWriteADTS) {
                        Log.e(TAG, "onOutputBufferAvailable:sampleRate " + codec.getOutputFormat().getInteger(MediaFormat.KEY_SAMPLE_RATE));
                        FileUtils.write(outputChannel, MediaUtil.synthesisADTS(bytes.length, codecLevel, codec.getOutputFormat().getInteger(MediaFormat.KEY_SAMPLE_RATE), codec.getOutputFormat().getInteger(MediaFormat.KEY_CHANNEL_COUNT)), bytes);
                    } else {
                        FileUtils.write(outputChannel, bytes);
                    }
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
