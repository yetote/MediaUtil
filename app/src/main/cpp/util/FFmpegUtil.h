//
// Created by ether on 2020/2/24.
//

#ifndef MEDIAUTIL_FFMPEGUTIL_H
#define MEDIAUTIL_FFMPEGUTIL_H

#include "LogUtil.h"

extern "C" {

#include <libavformat/avformat.h>
}

#define FFmpegUtil_TAG "FFmpegUtil"

class FFmpegUtil {
public:
    static void dumpFmt(const AVFormatContext &pFmtCtx);

    static void dumpOutFmt(const AVOutputFormat &pOutFmt);
    static void dumpCodecCtx(const AVCodecContext &pCodecCtx);

private:
};


#endif //MEDIAUTIL_FFMPEGUTIL_H
