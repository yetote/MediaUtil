//
// Created by ether on 2020/2/24.
//

#include "FFmpegUtil.h"

void FFmpegUtil::dumpOutFmt(const AVOutputFormat &pOutFmt) {
    LOGE(FFmpegUtil_TAG, "---------------------------FFmpeg AVOutFormat---------------------------");
    LOGE(FFmpegUtil_TAG, "AVOutFormat.name= %s", pOutFmt.name);
    LOGE(FFmpegUtil_TAG, "AVOutFormat.long_name = %s", pOutFmt.long_name);
    LOGE(FFmpegUtil_TAG, "AVOutFormat.mime_type = %s", pOutFmt.mime_type);
    LOGE(FFmpegUtil_TAG, "AVOutFormat.extensions = %s", pOutFmt.extensions);
    LOGE(FFmpegUtil_TAG, "AVOutFormat.audio_codec = %d", pOutFmt.audio_codec);
    LOGE(FFmpegUtil_TAG, "AVOutFormat.video_codec = %d", pOutFmt.video_codec);
    LOGE(FFmpegUtil_TAG, "AVOutFormat.subtitle_codec = %d", pOutFmt.subtitle_codec);
    LOGE(FFmpegUtil_TAG, "---------------------------END---------------------------");
}

void FFmpegUtil::dumpCodecCtx(const AVCodecContext &pCodecCtx) {
//    LOGE(FFmpegUtil_TAG, "---------------------------FFmpeg AVCodecContext---------------------------");
//    LOGE(FFmpegUtil_TAG, "AVCodecContext.name= %s", pCodecCtx.codec_type);
//    LOGE(FFmpegUtil_TAG, "---------------------------END---------------------------");
}
