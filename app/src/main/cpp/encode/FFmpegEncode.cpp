//
// Created by ether on 2020/2/24.
//


#include "FFmpegEncode.h"

void FFmpegEncode::encodeAudio(const std::string &inputPath,
                               const std::string &outputPath,
                               const std::string &mime, int channelCount, int sampleRate,
                               int bitRate) {
    pOutFmtCtx = av_guess_format(nullptr, outputPath.c_str(), mime.c_str());
    uint16_t *frame_buf;
    FFmpegUtil::dumpOutFmt(*pOutFmtCtx);
    pCodec = avcodec_find_encoder(pOutFmtCtx->audio_codec);
    if (!pCodec) {
        LOGE(FFmpegEncode_TAG, "%s:未找到对应的编码器", __func__);
        return;
    }
    pCodecCtx = avcodec_alloc_context3(pCodec);
    pCodecCtx->sample_rate = sampleRate;
    pCodecCtx->channel_layout = channelCount == 2 ? AV_CH_LAYOUT_STEREO : AV_CH_LAYOUT_MONO;
    pCodecCtx->bit_rate = bitRate;
    pCodecCtx->sample_fmt = AV_SAMPLE_FMT_S16;
    pCodecCtx->channels = av_get_channel_layout_nb_channels(pCodecCtx->channel_layout);//声道数

    avcodec_open2(pCodecCtx, pCodec, nullptr);

    packet = av_packet_alloc();
    pFrame = av_frame_alloc();

    if (!packet || !pFrame) {
        LOGE(FFmpegEncode_TAG, "%s:packet||frame is nullptr", __func__);
        return;
    }

    pFrame->nb_samples = pCodecCtx->frame_size;
    pFrame->format = pCodecCtx->sample_fmt;
    pFrame->channel_layout = pCodecCtx->channel_layout;
    pFrame->channels = pCodecCtx->channels;
    pFrame->sample_rate = pCodecCtx->sample_rate;
    auto size = av_samples_get_buffer_size(nullptr, pCodecCtx->channels, pCodecCtx->sample_rate,
                                           pCodecCtx->sample_fmt,
                                           1);
//    auto size = av_frame_get_buffer(pFrame, 0);
    LOGE(FFmpegEncode_TAG, "%s:size=%d", __func__, size);
    frame_buf = (uint16_t *) av_malloc(size);
    avcodec_fill_audio_frame(pFrame, pCodecCtx->channels, pCodecCtx->sample_fmt,(const uint8_t*)frame_buf, size, 1);

    auto infile = fopen(inputPath.c_str(), "rb+");
    auto outfile = fopen(outputPath.c_str(), "wb+");

    while (!feof(infile)) {

        auto ret = fread(frame_buf, size, 1, infile);
        LOGE(FFmpegEncode_TAG, "%s: file ret=%d", __func__, ret);
        ret = avcodec_send_frame(pCodecCtx, pFrame);
        LOGE(FFmpegEncode_TAG, "%s:ret=%d", __func__, ret);
        while (ret >= 0) {
            ret = avcodec_receive_packet(pCodecCtx, packet);
            if (ret == AVERROR(EAGAIN) || ret == AVERROR_EOF)
                return;
            else if (ret < 0) {
                fprintf(stderr, "Error encoding audio frame\n");
            }

            fwrite(packet->data, 1, packet->size, outfile);
            av_packet_unref(packet);
        }
    }
    LOGE(FFmpegEncode_TAG, "%s:done", __func__);

}

