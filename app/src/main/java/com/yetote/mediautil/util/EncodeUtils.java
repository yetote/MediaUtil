package com.yetote.mediautil.util;

import android.util.Log;

import com.yetote.mediautil.interfaces.EncodeProgressCallback;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class EncodeUtils {
    private static final String TAG = "EncodeUtils";
    FileInputStream inputStream;
    FileOutputStream outputStream;
    FileChannel inputChannel, outputChannel;
    public static final int ENCODE_TYPE_HARDWARE = 0X0001;
    public static final int ENCODE_TYPE_SOFTWARE = 0X0002;

    public static final int ENCODE_API_TYPE_JAVA = 0X1001;
    public static final int ENCODE_API_TYPE_NATIVE = 0X1002;

    public static final int ENCODE_MEDIACODEC_TYPE_SYNCHRONOUS = 0X2001;
    public static final int ENCODE_MEDIACODEC_TYPE_ASYNCHRONOUS = 0X2002;

    EncodeProgressCallback progressCallback;

    boolean isHardWare = true;
    String inputPath, outputPath;

    public EncodeUtils setProgressCallback(EncodeProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
        return this;
    }

    public EncodeUtils() {

    }

    public EncodeUtils setInputPath(String path) {
        this.inputPath = path;
        try {
            FileUtils.checkState(FileUtils.checkFile(path), FileUtils.FILE_STATE_SUCCESS);
            inputStream = new FileInputStream(path);
            inputChannel = inputStream.getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return this;
    }

    public EncodeUtils setCodecType(int codecType) {
        if (codecType != ENCODE_MEDIACODEC_TYPE_SYNCHRONOUS && codecType != ENCODE_MEDIACODEC_TYPE_ASYNCHRONOUS) {
            throw new IllegalArgumentException("codecType 仅被允许为 HW_ENCODEC_TYPE_SYNCHRONOUS 或 HW_ENCODEC_TYPE_ASYNCHRONOUS ");
        }
        return this;
    }

    public EncodeUtils setOutputPath(String path) {
        this.outputPath = path;
        try {
            FileUtils.checkState(FileUtils.checkFile(path), FileUtils.FILE_STATE_SUCCESS);
            outputStream = new FileOutputStream(path);
            outputChannel = outputStream.getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return this;
    }

    public EncodeUtils setHardware(boolean isHardware) {
        isHardWare = isHardware;
        return this;
    }

    public EncodeUtils setAPI(int APIType) {
        return this;
    }

    public void encodeAudio(String mime, int channelCount, int sampleRate, String codecName, String codecLevel, boolean isWriteADTS) {
        if (isHardWare) {
            HardWareCodec.encodeAudio(inputChannel, outputChannel, codecName, mime, sampleRate, channelCount, true, codecLevel, isWriteADTS, progressCallback);
        } else {
            Log.e(TAG, "encodeAudio: "+inputPath );
            Log.e(TAG, "encodeAudio: "+outputPath );
            Log.e(TAG, "encodeAudio: "+mime );
            FFmpegUtil.encodeAudio(inputPath, outputPath, mime);
        }
    }

    public void destroy() {
        try {
            if (inputChannel != null) {
                inputChannel.close();
            }
            if (outputChannel != null) {
                outputChannel.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
