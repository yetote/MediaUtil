//
// Created by ether on 2019/12/26.
//

#ifndef FFMPEGDEMO_FFMPEGDEMODE_H
#define FFMPEGDEMO_FFMPEGDEMODE_H

#include <string>

#define FFmpegDemode_TAG "FFmpegDemode"
extern "C" {
#include "../include/libavformat/avformat.h"
#include "../include/libavutil/avutil.h"
};

class FFmpegDemode {
public:
    FFmpegDemode();

    virtual ~FFmpegDemode();

    void destroy();

    void demuxing(std::string in, std::string aout, std::string vout);

private:
    AVFormatContext *pFmtCtx;
    AVCodec *pCodec;
    AVCodecContext *pCodecCtx;
};


#endif //FFMPEGDEMO_FFMPEGDEMODE_H
