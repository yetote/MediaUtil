package com.yetote.mediautil.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class FileUtils {
    private static final String TAG = "FileUtil";
    private static ByteBuffer byteBuffer;

    public static final int FILE_STATE_SUCCESS = 0x0000;

    public static final int FILE_STATE_PREPARED = 0x0001;
    public static final int FILE_STATE_CLOSED = 0x0002;

    public static final int FILE_STATE_UN_PREPARED = 0x0003;
    public static final int FILE_STATE_UN_EXIST = 0x0004;

    public static final int FILE_STATE_END = 0x0005;
    public static final int FILE_STATE_START = 0x0006;

    public static final int FILE_STATE_READING = 0x0007;

    public static final int FILE_STATE_NULL_PATH = 0x0008;

    public static final String[] errTable = new String[]{
            "FILE_STATE_SUCCESS", "FILE_STATE_PREPARED", "FILE_STATE_CLOSED",
            "FILE_STATE_UN_PREPARED", "FILE_STATE_UN_EXIST",
            "FILE_STATE_END", "FILE_STATE_START", "FILE_STATE_READING", "FILE_STATE_NULL_PATH"
    };
    public static int FILE_STATE = -1;

    public static int checkFile(String path) {
        if (path == null) {
            return FILE_STATE_NULL_PATH;
        }
        File file = new File(path);
        if (!file.exists()) {
            return FILE_STATE_UN_EXIST;
        }
        return FILE_STATE_SUCCESS;
    }

    public static int read(FileChannel fileChannel, int pos, byte[]... arrays) {
        if (fileChannel == null) {
            Log.e(TAG, "read: 未进行初始化");
            return FILE_STATE_UN_PREPARED;
        }
        if (byteBuffer == null) {
            int length = 0;
            for (byte[] arr : arrays) {
                length += arr.length;
            }
            byteBuffer = ByteBuffer.allocate(length).order(ByteOrder.nativeOrder());
        }

        try {
            if (pos >= fileChannel.size()) {
                return FILE_STATE_END;
            }
            if (pos < 0) {
                return FILE_STATE_START;
            }
            byteBuffer.clear();
            fileChannel.position(pos);
            Log.e(TAG, "read: pos" + fileChannel.position());
            fileChannel.read(byteBuffer);
            byteBuffer.flip();
            for (byte[] dataArr : arrays) {
                Log.e(TAG, "read: buffer 容量" + byteBuffer.limit());
                byteBuffer.get(dataArr);
                Log.e(TAG, "read: " + Arrays.toString(dataArr));
            }

        } catch (IOException e) {
            Log.e(TAG, "read: " + e.toString());
            e.printStackTrace();
        }

        return FILE_STATE_READING;
    }

    public static void close(FileChannel fileChannel, InputStream inputStream) {
        try {
            if (fileChannel != null) {
                fileChannel.close();
                fileChannel = null;
            }

            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }

            if (byteBuffer != null) {
                byteBuffer.clear();
                byteBuffer = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        FILE_STATE = FILE_STATE_CLOSED;
        Log.e(TAG, "close: 销毁FileUtil");
    }

    public static boolean checkState(int state, int aims) {
        if (state != aims)
            throw new IllegalStateException(errTable[state]);
        return true;
    }

    public static boolean createFile(String path) {
        if (path == null) {
            Log.e(TAG, "prepare: path为null");
            return false;
        }
        File file = new File(path);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    public static boolean checkState(int state, int aims, Context context) {
        if (state != aims) {
            Toast.makeText(context, errTable[state], Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
