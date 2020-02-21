package com.yetote.mediautil.util;

import android.util.Log;

public class MediaUtil {
    private static final String TAG = "MediaUtil";
    static int[][] sampleRateTable = new int[][]{
            //00 01 02  03  04  05  06  07  08  09
            {-1, -1, -1, -1, -1, -1, -1, 12, 11, -1},
            {-1, 10, 9, -1, -1, -1, 8, -1, -1, -1},
            {-1, -1, 7, -1, 6, -1, -1, -1, -1, -1},
            {-1, -1, 5, -1, -1, -1, -1, -1, -1, -1},
            {-1, -1, -1, -1, 4, -1, -1, -1, 3, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {-1, -1, -1, -1, 2, -1, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, 1, -1},
            {-1, -1, -1, -1, -1, -1, 0, -1, -1, -1},
    };

    /**
     * 合成adts
     *
     * @return adts数组
     */
    public static byte[] synthesisADTS(int packetLength, int aacObjectType, int sampleRate, int channelCount) {
        //传递进来的为裸流数据，需要加上adts占用的7byte
        packetLength += 7;
        //AAC编码级别,该值与MediaCodecInfo.CodecProfileLevel.AACObjectXX的值对应
        int profile = aacObjectType;
        //采样率，采用双层查表法查找
        int freqIdx = sampleRateTable[sampleRate / 10000][sampleRate / 1000 % 10];
        //声道数，这里可以直接传数字
        int chanCfg = channelCount;

        Log.e(TAG, "synthesisADTS: " + freqIdx);
        /*int avpriv_mpeg4audio_sample_rates[] = {
            96000, 88200, 64000, 48000, 44100, 32000,
                    24000, 22050, 16000, 12000, 11025, 8000, 7350
        };
        channel_configuration: 表示声道数chanCfg
        0: Defined in AOT Specifc Config
        1: 1 channel: front-center
        2: 2 channels: front-left, front-right
        3: 3 channels: front-center, front-left, front-right
        4: 4 channels: front-center, front-left, front-right, back-center
        5: 5 channels: front-center, front-left, front-right, back-left, back-right
        6: 6 channels: front-center, front-left, front-right, back-left, back-right, LFE-channel
        7: 8 channels: front-center, front-left, front-right, side-left, side-right, back-left, back-right, LFE-channel
        8-15: Reserved
        */
        byte[] adts = new byte[7];
        // fill in ADTS data
        adts[0] = (byte) 0xFF;
        adts[1] = (byte) 0xF9;
        adts[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        adts[3] = (byte) (((chanCfg & 3) << 6) + (packetLength >> 11));
        adts[4] = (byte) ((packetLength & 0x7FF) >> 3);
        adts[5] = (byte) (((packetLength & 7) << 5) + 0x1F);
        adts[6] = (byte) 0xFC;
        return adts;
    }

    public static void aacObjectType() {
        //NULL表示
        int AACObjectMain = 1;
        int AACObjectLC = 2;
        int AACObjectSSR = 3;
        int AACObjectLTP = 4;
        int AACObjectHE = 5;
        int AACObjectScalable = 6;
        int AACObjectERLC = 17;
        int AACObjectERScalable = 20;
        int AACObjectLD = 23;
        int AACObjectHE_PS = 29;
        int AACObjectELD = 39;
        /** xHE-AAC (includes USAC) */
        int AACObjectXHE = 42;
    }
}
