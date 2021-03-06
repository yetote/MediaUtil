#include <jni.h>
#include <string>
#include "decode/FFmpegDemode.h"
#include "util/LogUtil.h"
#include "encode/FFmpegEncode.h"

JavaVM *jvm;
FFmpegDemode *fFmpegDecode;

void demuxing(JNIEnv *env, jobject obj, jstring in_, jstring aout_, jstring vout_) {
    if (!fFmpegDecode) {
        fFmpegDecode = new FFmpegDemode();
    }
    if (aout_ == NULL) {
        LOGE("native", "%s:aout_为空", __func__);
        return;
    }
    std::string in = env->GetStringUTFChars(in_, JNI_FALSE);
    LOGE("native", "%s:in=%s", __func__, in.c_str());
    std::string aout = env->GetStringUTFChars(aout_, JNI_FALSE);
    std::string vout = env->GetStringUTFChars(vout_, JNI_FALSE);

    fFmpegDecode->demuxing(in, aout, vout);

}

void encodeAudio4FFmpeg(JNIEnv *env, jclass obj, jstring inputPath_, jstring outputPath_,
                        jstring mime_, jint channelCount, jint sampleRate, jint bitRate) {
    std::string inputPath = env->GetStringUTFChars(inputPath_, JNI_FALSE);
    std::string outputPath = env->GetStringUTFChars(outputPath_, JNI_FALSE);
    std::string mime = env->GetStringUTFChars(mime_, JNI_FALSE);

    auto ffEncodeAudioSpr = std::make_shared<FFmpegEncode>();
    ffEncodeAudioSpr->encodeAudio(inputPath, outputPath, mime, channelCount, sampleRate, bitRate);

//    env->ReleaseStringUTFChars(mime_, mime.c_str());
//    env->ReleaseStringUTFChars(outputPath_, outputPath.c_str());
//    env->ReleaseStringUTFChars(inputPath_, inputPath.c_str());
}

void destroy(JNIEnv *env, jobject obj) {
    if (fFmpegDecode) {
        fFmpegDecode->destroy();
    }
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    jvm = vm;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    JNINativeMethod methods[]{
            {"encodeAudio", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;III)V", (void *) encodeAudio4FFmpeg},
    };
    jclass jlz = env->FindClass("com/yetote/mediautil/util/FFmpegUtil");
    env->RegisterNatives(jlz, methods, sizeof(methods) / sizeof(methods[0]));
    return JNI_VERSION_1_6;
}

