//
// Created by ether on 2020/2/24.
//



#include "FFmpegEncode.h"
// 申请智能指针变量
#define NEW_PTR(T, P, V, Fn) T *P = V; std::shared_ptr<T> P##P(P, [&P](T *){if(P != nullptr){Fn;}})
// 打印异常信息，并退出main函数
#define FATAL(M, ...) LOGE(FFmpegEncode_TAG, M); return

//    @formatter:off
void FFmpegEncode::encodeAudio(const std::string &inputPath, const std::string &outputPath,const std::string &mime, int channelCount, int sampleRate,int bitRate) {
//    @formatter:on
    // 申请一个输出的上下文
    NEW_PTR(AVFormatContext, fmt_ctx, nullptr, avio_closep(&fmt_ctx->pb);
            avformat_close_input(&fmt_ctx));
    avformat_alloc_output_context2(&fmt_ctx, nullptr, nullptr, outputPath.c_str());
    if (fmt_ctx == nullptr) {
        FATAL("alloc output format context failed.");
    }

    // 查询编码器
    AVCodec *audio_enc;
    if ((audio_enc = avcodec_find_encoder(fmt_ctx->oformat->audio_codec)) == nullptr) {
        FATAL("find audio encoder failed.");
    }

    AVStream *audio_stream = avformat_new_stream(fmt_ctx, audio_enc);
    audio_stream->id = fmt_ctx->nb_streams - 1;

    // 为编码器申请上下文
    NEW_PTR(AVCodecContext, audio_enc_ctx, nullptr, avcodec_free_context(&audio_enc_ctx));
    if ((audio_enc_ctx = avcodec_alloc_context3(audio_enc)) == nullptr) {
        FATAL("allocate audio enc context failed.");
    }

    // 为编码器配置编码参数
    audio_enc_ctx->sample_fmt = audio_enc->sample_fmts ? audio_enc->sample_fmts[0]
                                                       : AV_SAMPLE_FMT_FLTP;
    audio_enc_ctx->bit_rate = 64000;
    audio_enc_ctx->sample_rate = 44100;
    audio_enc_ctx->channel_layout = AV_CH_LAYOUT_STEREO;
    audio_enc_ctx->channels = av_get_channel_layout_nb_channels(audio_enc_ctx->channel_layout);
    audio_enc_ctx->time_base = AVRational{1, audio_enc_ctx->sample_rate};

    audio_stream->time_base = audio_enc_ctx->time_base;

    if (fmt_ctx->oformat->flags & AVFMT_GLOBALHEADER) {
        audio_enc_ctx->flags |= AV_CODEC_FLAG_GLOBAL_HEADER;
    }

    // 打开编码器
    if (avcodec_open2(audio_enc_ctx, audio_enc, nullptr) < 0) {
        FATAL("codec open failed.");
    }

    if (avcodec_parameters_from_context(audio_stream->codecpar, audio_enc_ctx) < 0) {
        FATAL("copy params failed.");
    }

    if (avio_open(&fmt_ctx->pb, outputPath.c_str(), AVIO_FLAG_WRITE) < 0) {
        FATAL("open dst file failed.");
    }

    // 写入头信息
    if (avformat_write_header(fmt_ctx, nullptr) < 0) {
        FATAL("write header failed.");
    }

    // 申请一个音频frame
    NEW_PTR(AVFrame, audio_frame, nullptr, av_frame_free(&audio_frame));
    if ((audio_frame = av_frame_alloc()) == nullptr) {
        FATAL("allocate frame failed.");
    }

    audio_frame->format = audio_enc_ctx->sample_fmt;
    audio_frame->channel_layout = audio_enc_ctx->channel_layout;
    audio_frame->nb_samples = audio_enc_ctx->frame_size;
    audio_frame->sample_rate = audio_enc_ctx->sample_rate;

    if (av_frame_get_buffer(audio_frame, 0) < 0) {
        FATAL("audio frame get buffer failed.");
    }

    // 创建一个frame，用来存储从pcm读取的数据
    NEW_PTR(AVFrame, buf_frame, nullptr, av_frame_free(&buf_frame));
    if ((buf_frame = av_frame_alloc()) == nullptr) {
        FATAL("allocate buf frame failed.");
    }
    buf_frame->format = AV_SAMPLE_FMT_S16;
    buf_frame->nb_samples = audio_frame->nb_samples;
    buf_frame->channel_layout = (uint64_t) av_get_default_channel_layout(2);
    buf_frame->sample_rate = 48000;

    if (av_frame_get_buffer(buf_frame, 0) < 0) {
        FATAL("create buf frame buffer failed.");
    }

    // 从pcm文件中读取适应音频帧的尺寸数据
    auto readSize = av_samples_get_buffer_size(nullptr, buf_frame->channels, buf_frame->nb_samples,
                                               (AVSampleFormat) buf_frame->format, 1);
    NEW_PTR(uint8_t, buf, (uint8_t *) av_malloc((size_t) readSize), av_freep(&buf));

    // 创建swr的上下文
    NEW_PTR(SwrContext, swr_ctx, swr_alloc(), swr_free(&swr_ctx));
    swr_alloc_set_opts(swr_ctx,
                       audio_frame->channel_layout,
                       (AVSampleFormat) audio_frame->format,
                       audio_frame->sample_rate,
                       av_get_default_channel_layout(2),
                       AV_SAMPLE_FMT_S16,
                       48000,
                       0, nullptr);
    swr_init(swr_ctx);

    // 申请一个packet，并初始化
    AVPacket pkt;
    av_init_packet(&pkt);
    pkt.data = nullptr;
    pkt.size = 0;

    NEW_PTR(FILE, input, nullptr, fclose(input));
    if ((input = fopen(inputPath.c_str(), "rb")) == nullptr) {
        FATAL("no readable file.");
    }

    // 循环读取frame数据
    int samples_count = 0;
    while (true) {
        // 用来编码的帧
        AVFrame *encode_frame = nullptr;

        if (fread(buf, 1, (size_t) readSize, input) < 0) {
            FATAL("read input file failed.");
        } else if (!feof(input)) {
            // 文件没有到结尾，则获取编码帧
            avcodec_fill_audio_frame(buf_frame, buf_frame->channels,
                                     (AVSampleFormat) buf_frame->format, buf, readSize, 1);

            swr_convert(swr_ctx, audio_frame->data, audio_frame->nb_samples,
                        (const uint8_t **) buf_frame->data, buf_frame->nb_samples);

            audio_frame->pts = av_rescale_q(samples_count, audio_stream->time_base,
                                            audio_enc_ctx->time_base);
            samples_count += audio_frame->nb_samples;

            encode_frame = audio_frame;
        } else {
            // 文件结束了，则发送一个空指针的frame，用来清空缓冲区
            encode_frame = nullptr;
        }

        // 发送一个frame
        if (avcodec_send_frame(audio_enc_ctx, encode_frame) < 0) {
            FATAL("send frame exception.");
        }
        // 接受编码完的内容
        while (true) {
            auto packet_ret = avcodec_receive_packet(audio_enc_ctx, &pkt);
            // 判断是否完全接受了packet
            if (packet_ret == AVERROR(EAGAIN) || packet_ret == AVERROR_EOF) {
                break;
            }
            // 检查是否接受异常
            if (packet_ret < 0) {
                FATAL("receive packet exception.");
            }

            av_packet_rescale_ts(&pkt, audio_enc_ctx->time_base, audio_stream->time_base);
            pkt.stream_index = audio_stream->index;

            av_interleaved_write_frame(fmt_ctx, &pkt);

            av_packet_unref(&pkt);
        }

        // 编码帧为空，则表示已经处理完所有的编码，退出该循环
        if (encode_frame == nullptr) break;
    }

    av_write_trailer(fmt_ctx);
    LOGE(FFmpegEncode_TAG,"%s:编码完成",__func__);
}

