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
    private static final int FILE_STATE_PREPARED = 0x0001;
    private static final int FILE_STATE_CLOSED = 0x0002;
    public static int FILE_STATE = -1;


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
            Log.e(TAG, "prepare: 异常" + e.toString());
            close();
        }
        FILE_STATE = FILE_STATE_PREPARED;
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
        if (byteBuffer == null) {
            int length = 0;
            for (byte[] arr : arrays) {
                length += arr.length;
            }
            byteBuffer = ByteBuffer.allocate(length).order(ByteOrder.nativeOrder());
        }
        try {
            fileChannel.read(byteBuffer);
            byteBuffer.flip();
            for (byte[] dataArr : arrays) {
                Log.e(TAG, "read: buffer 容量" + byteBuffer.limit());
                byteBuffer.get(dataArr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void close() {
        try {
            if (fileChannel != null) {
                fileChannel.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

            if (byteBuffer != null) {
                byteBuffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        FILE_STATE = FILE_STATE_CLOSED;
        Log.e(TAG, "close: 销毁FileUtil");
    }

}
