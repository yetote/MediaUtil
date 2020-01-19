package com.yetote.mediautil.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class FileUtil {
    private static FileChannel fileChannel;
    private static FileInputStream inputStream;
    private static final String TAG = "FileUtil";
    private static ByteBuffer byteBuffer;

    public static boolean prepare(String path) {
        if (path == null) {
            Log.e(TAG, "prepare: path为null");
            return false;
        }
        File file = new File(path);
        if (!file.exists()) {
            Log.e(TAG, "prepare: 文件不存在");
            return false;
        }
        try {
            inputStream = new FileInputStream(path);
            fileChannel = inputStream.getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return true;
    }

    public static boolean prepare(String path, int size) {
        byteBuffer = ByteBuffer.allocate(size).order(ByteOrder.nativeOrder());
        return prepare(path);
    }


    public static boolean read(byte[]... arrays) {
        if (inputStream == null || fileChannel == null) {
            Log.e(TAG, "read: 未进行初始化");
            return false;
        }

        for (byte[] dataArr : arrays) {

        }
    }

    private static void close() {
        try {
            if (fileChannel != null) {
                fileChannel.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
