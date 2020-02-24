package com.yetote.mediautil.util;

public class FFmpegUtil {
    static {
        System.loadLibrary("native-lib");
    }

    public static native void encodeAudio(String inputPath, String outputPath, String mime);
}
