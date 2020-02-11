package com.yetote.mediautil.util.yuvutil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class YUVUtil {
    private static final String TAG = "YUVUtil";

    public enum YUV_TYPE {
        YUV_420P,
    }

    /**
     * 根据宽、高计算出yuv各分量的大小
     *
     * @param w    宽
     * @param h    高
     * @param type YUV类型
     * @return yuv分量长度数组。0为Y分量长度，1为U分量，2为V分量。类型不存在时，返回负数数组
     */
    public static int[] distributionYUV(int w, int h, YUV_TYPE type) {
        int arr[] = new int[3];

        if (w <= 0 || h <= 0) {
            arr[0] = -1;
            arr[1] = -1;
            arr[2] = -1;
            Log.e(TAG, "distributionYUV: 宽高非法");
            return arr;
        }

        switch (type) {
            case YUV_420P:
                arr[0] = w * h;
                arr[1] = w * h / 4;
                arr[2] = w * h / 4;
                break;
            default:
                arr[0] = -1;
                arr[1] = -1;
                arr[2] = -1;
                Log.e(TAG, "distributionYUV: 未定义该类型");
                break;
        }
        return arr;
    }

    public static Bitmap yuv2bitmap(int w, int h, byte[] yuv) {
        YuvImage image = new YuvImage(yuv, ImageFormat.YUV_420_888, w, h, null);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compressToJpeg(new Rect(0, 0, w, h), 80, stream);

        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
        //TODO：此处可以对位图进行处理，如显示，保存等
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp;
    }
}
