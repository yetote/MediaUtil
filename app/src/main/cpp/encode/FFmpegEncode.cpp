//
// Created by ether on 2020/2/24.
//


#include "FFmpegEncode.h"


void FFmpegEncode::encodeAudio(const std::string &inputPath,
                               const std::string &outputPath,
                               const std::string &mime) {
    pOutFmtCtx = av_guess_format(nullptr, outputPath.c_str(), mime.c_str());
    FFmpegUtil::dumpOutFmt(*pOutFmtCtx);
    LOGE(FFmpegEncode_TAG, "%s:", __func__);
}

