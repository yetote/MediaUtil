### MediaCodec
- configure时提示
``` 
Codec reported err 0xffffffea, actionCode 0, while in state 3
configure failed with err 0xffffffea, resetting...
```
请检查MediaFormat中所需参数是否完整
```
MediaFormat mediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelCount);
mediaFormat.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_AUDIO_AAC);
mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 2);
mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, 48000);
mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, minBufferSize * 2);
mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
```
- 调用硬件厂商提供的编码器时出现Error code in android.media.MediaCodec$CodecException 0x80000000
在杀掉进程重进后，该error会消失，然而再次杀掉进程后，该error又会出现。二者始终循环。
原因为在编码完成后并没有调用mediaCodec.stop()，推测为杀掉进程后，硬件并没有及时响应，仍处于start状态,所以第二次进入会崩溃
解决办法就是在编码完成后加入mediacodec的stop和release方法。