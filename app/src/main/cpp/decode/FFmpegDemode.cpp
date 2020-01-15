//
// Created by ether on 2019/12/26.
//


#include "FFmpegDemode.h"
#include "../util/LogUtil.h"


using namespace std;

FFmpegDemode::FFmpegDemode() {}

FFmpegDemode::~FFmpegDemode() {

}

void FFmpegDemode::destroy() {

}

void
openCodec(AVFormatContext *pFmtCtx, AVCodecContext *pCodecCtx, AVCodec *pCodec,
          AVMediaType mediaType) {
    if (!pFmtCtx) {
        LOGE(FFmpegDemode_TAG, "%s:AVFormatContext is null", __func__);
        return;
    }
    int ret;
    ret = av_find_best_stream(pFmtCtx, mediaType, -1, -1, &pCodec, 0);
    if (ret < 0) {
        LOGE(FFmpegDemode_TAG, "%s:can't find media stream , err = %s", __func__, av_err2str(ret));
        return;
    }
    pCodecCtx = avcodec_alloc_context3(pCodec);
    if (!pCodecCtx) {
        LOGE(FFmpegDemode_TAG, "%s:can't to alloc pCodecCtx", __func__);
        return;
    }
    ret = avcodec_open2(pCodecCtx, pCodec, nullptr);
    if (ret < 0) {
        LOGE(FFmpegDemode_TAG, "%s:open codec fail", __func__);
        return;
    }

}

void FFmpegDemode::demuxing(std::string in, std::string aout, std::string vout) {
    int ret;
    if (in.empty() || aout.empty() || vout.empty()) {
        LOGE(FFmpegDemode_TAG, "%s:the input path is null", __func__);
        return;
    }
    pFmtCtx = avformat_alloc_context();
    LOGE(FFmpegDemode_TAG, "%s:path = %s", __func__, in.c_str());
    ret = avformat_open_input(&pFmtCtx, in.c_str(), nullptr, nullptr);
    if (ret < 0) {
        LOGE(FFmpegDemode_TAG, "%s:fail to alloc avFmtCtx err=%s", __func__, av_err2str(ret));
        return;
    }
    openCodec(pFmtCtx, pCodecCtx, pCodec, AVMEDIA_TYPE_VIDEO);
    if(!pCodec){
        LOGE(FFmpegDemode_TAG,"%s:open codec fail",__func__);
        return;
    }
    LOGE(FFmpegDemode_TAG,"%s: open codec success",__func__);
}
