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

    public static final int FILE_STATE_SUCCESS = 0x0000;

    public static final int FILE_STATE_PREPARED = 0x0001;
    public static final int FILE_STATE_CLOSED = 0x0002;

    public static final int FILE_STATE_UN_PREPARED = 0x0003;
    public static final int FILE_STATE_UN_EXIST = 0x0004;

    public static final int FILE_STATE_END = 0x0005;
    public static final int FILE_STATE_START = 0x0006;

    public static final int FILE_STATE_READING = 0x0007;
    public static final int FILE_STATE_WRITING = 0x0008;

    public static final int FILE_STATE_NULL_PATH = 0x0009;

    public static final String[] errTable = new String[]{
            "FILE_STATE_SUCCESS", "FILE_STATE_PREPARED", "FILE_STATE_CLOSED",
            "FILE_STATE_UN_PREPARED", "FILE_STATE_UN_EXIST", "FILE_STATE_END",
            "FILE_STATE_START", "FILE_STATE_READING", "FILE_STATE_WRITING",
            "FILE_STATE_NULL_PATH"
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


        int length = 0;
        for (byte[] arr : arrays) {
            length += arr.length;
        }

        try {
            length = (int) Math.min(fileChannel.size() - fileChannel.position(), length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.allocate(length).order(ByteOrder.nativeOrder());
        try {
            if (pos >= fileChannel.size()) {
                return FILE_STATE_END;
            }
            if (pos < 0) {
                return FILE_STATE_START;
            }
            buffer.clear();
            fileChannel.position(pos);
            Log.e(TAG, "read: pos" + fileChannel.position());
            fileChannel.read(buffer);
            buffer.flip();
            for (byte[] dataArr : arrays) {
                Log.e(TAG, "read: buffer 容量" + buffer.limit());
                buffer.get(dataArr, 0, Math.min(buffer.limit() - buffer.position(), dataArr.length));
                Log.e(TAG, "read: " + Arrays.toString(dataArr));
            }

        } catch (IOException e) {
            Log.e(TAG, "read: " + e.toString());
            e.printStackTrace();
        }

        return FILE_STATE_READING;
    }

    public static int write(FileChannel fileChannel, byte[]... arrays) {
        if (fileChannel == null) {
            Log.e(TAG, "read: 未进行初始化");
            return FILE_STATE_UN_PREPARED;
        }
        int length = 0;
        for (byte[] arr : arrays) {
            length += arr.length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(length).order(ByteOrder.nativeOrder());
        for (byte[] dataArr : arrays) {
            buffer.put(dataArr);
        }
        return write(fileChannel, buffer);
    }

    public static int write(FileChannel fileChannel, ByteBuffer buffer) {
        if (fileChannel == null) {
            Log.e(TAG, "read: 未进行初始化");
            return FILE_STATE_UN_PREPARED;
        }
        buffer.flip();
        try {
            Log.e(TAG, "write: buffer limit" + buffer.limit());
            Log.e(TAG, "write: buffer position" + buffer.position());
            fileChannel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FILE_STATE_WRITING;
    }

    public static void close(FileChannel fileChannel, InputStream inputStream) {
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
        try {
            File file = new File(path);
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
