//
// Created by ether on 2020/2/24.
//

#ifndef MEDIAUTIL_FFMPEGENCODE_H
#define MEDIAUTIL_FFMPEGENCODE_H

#include <string>
#include "../util/LogUtil.h"
#include "../util/FFmpegUtil.h"

extern "C" {
#include <libavformat/avformat.h>
#include <libswresample/swresample.h>
};

#define FFmpegEncode_TAG "FFmpegEncode"

class FFmpegEncode {
public:
    FFmpegEncode() = default;

   void encodeAudio(const std::string &inputPath, const std::string &outputPath,
                const std::string &mime,
                int channelCount, int sampleRate, int bitRate);
private:
    AVFormatContext *pFmtCtx = nullptr;
    AVCodecContext *pCodecCtx = nullptr;
    AVOutputFormat *pOutFmtCtx = nullptr;
    AVCodec *pCodec = nullptr;
    AVPacket *packet= nullptr;
    AVFrame *pFrame= nullptr;


};


#endif //MEDIAUTIL_FFMPEGENCODE_H
