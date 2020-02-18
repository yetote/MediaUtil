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