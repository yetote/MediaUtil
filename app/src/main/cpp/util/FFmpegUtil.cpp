//
// Created by ether on 2020/2/24.
//

#include "FFmpegUtil.h"

void FFmpegUtil::dumpOutFmt(const AVOutputFormat &pOutFmt) {
    LOGE(FFmpegUtil_TAG, "---------------------------FFmpeg START---------------------------");
    LOGE(FFmpegUtil_TAG, "%s:AVOutFormat.name= %s", __func__, pOutFmt.name);
    LOGE(FFmpegUtil_TAG, "%s:AVOutFormat.long_name = %s", __func__, pOutFmt.long_name);
    LOGE(FFmpegUtil_TAG, "%s:AVOutFormat.mime_type = %s", __func__, pOutFmt.mime_type);
    LOGE(FFmpegUtil_TAG, "%s:AVOutFormat.extensions = %s", __func__, pOutFmt.extensions);
    LOGE(FFmpegUtil_TAG, "%s:AVOutFormat.audio_codec = %d", __func__, pOutFmt.audio_codec);
    LOGE(FFmpegUtil_TAG, "%s:AVOutFormat.video_codec = %d", __func__, pOutFmt.audio_codec);
    LOGE(FFmpegUtil_TAG, "%s:AVOutFormat.subtitle_codec = %d", __func__, pOutFmt.audio_codec);
    LOGE(FFmpegUtil_TAG, "---------------------------FFmpeg END---------------------------");


}
