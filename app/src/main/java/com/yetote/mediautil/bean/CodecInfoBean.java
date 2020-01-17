package com.yetote.mediautil.bean;

public class CodecInfoBean {
    /**
     * 支持的最大的并发实例化数量
     */
    private int maxInstances;

    /**
     * 支持的mimetype类型
     */
    private String mimeType;
    /**
     * 默认的mimetype类型
     */
    private String defaultMimeType;

    private AudioCodecInfo audioCodecInfo;
    private VideoCodecInfo videoCodecInfo;
    private EncoderInfo encoderInfo;

    public CodecInfoBean() {
    }

    public int getMaxInstances() {
        return maxInstances;
    }

    public void setMaxInstances(int maxInstances) {
        this.maxInstances = maxInstances;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDefaultMimeType() {
        return defaultMimeType;
    }

    public void setDefaultMimeType(String defaultMimeType) {
        this.defaultMimeType = defaultMimeType;
    }

    public AudioCodecInfo getAudioCodecInfo() {
        return audioCodecInfo;
    }

    public void setAudioCodecInfo(AudioCodecInfo audioCodecInfo) {
        this.audioCodecInfo = audioCodecInfo;
    }

    public VideoCodecInfo getVideoCodecInfo() {
        return videoCodecInfo;
    }

    public void setVideoCodecInfo(VideoCodecInfo videoCodecInfo) {
        this.videoCodecInfo = videoCodecInfo;
    }

    public EncoderInfo getEncoderInfo() {
        return encoderInfo;
    }

    public void setEncoderInfo(EncoderInfo encoderInfo) {
        this.encoderInfo = encoderInfo;
    }

    @Override
    public String toString() {
        return "CodecInfoBean{" +
                "maxInstances=" + maxInstances +
                "\n, mimeType='" + mimeType + '\'' +
                "\n, defaultMimeType='" + defaultMimeType + '\'' +
                "\n, audioCodecInfo=" + ((audioCodecInfo == null) ? "null" : audioCodecInfo.toString()) +
                "\n, videoCodecInfo=" + ((videoCodecInfo == null) ? "null" : videoCodecInfo.toString()) +
                "\n, encoderInfo=" + ((encoderInfo == null) ? "null" : encoderInfo.toString()) +
                '}';
    }

    public class AudioCodecInfo {
        String bitRate;
        int maxInputChannelCount;
        String supportedSampleRateRange;
        String supportedSampleRates;

        public AudioCodecInfo(String bitRate, int maxInputChannelCount, String supportedSampleRateRange, String supportedSampleRates) {
            this.bitRate = bitRate;
            this.maxInputChannelCount = maxInputChannelCount;
            this.supportedSampleRateRange = supportedSampleRateRange;
            this.supportedSampleRates = supportedSampleRates;
        }

        public String getBitRate() {
            return bitRate;
        }

        public void setBitRate(String bitRate) {
            this.bitRate = bitRate;
        }

        public int getMaxInputChannelCount() {
            return maxInputChannelCount;
        }

        public void setMaxInputChannelCount(int maxInputChannelCount) {
            this.maxInputChannelCount = maxInputChannelCount;
        }

        public String getSupportedSampleRateRange() {
            return supportedSampleRateRange;
        }

        public void setSupportedSampleRateRange(String supportedSampleRateRange) {
            this.supportedSampleRateRange = supportedSampleRateRange;
        }

        public String getSupportedSampleRates() {
            return supportedSampleRates;
        }

        public void setSupportedSampleRates(String supportedSampleRates) {
            this.supportedSampleRates = supportedSampleRates;
        }

        @Override
        public String toString() {
            return "AudioCodecInfo{" +
                    "bitRate='" + bitRate + '\'' +
                    "\n, maxInputChannelCount=" + maxInputChannelCount +
                    "\n, supportedSampleRate='" + supportedSampleRateRange + '\'' +
                    "\n, supportedSampleRates='" + supportedSampleRates + '\'' +
                    '}';
        }
    }

    public class VideoCodecInfo {
        String bitRate;
        int widthAlignment;
        int heightAlignment;
        String supportedFrameRates;
        String supportedWidths;
        String supportedHeights;
        String supportedPerformancePoints;

        public VideoCodecInfo(String bitRate, int widthAlignment, int heightAlignment, String supportedFrameRates, String supportedWidths, String supportedHeights) {
            this.bitRate = bitRate;
            this.widthAlignment = widthAlignment;
            this.heightAlignment = heightAlignment;
            this.supportedFrameRates = supportedFrameRates;
            this.supportedWidths = supportedWidths;
            this.supportedHeights = supportedHeights;
        }

        public String getBitRate() {
            return bitRate;
        }

        public void setBitRate(String bitRate) {
            this.bitRate = bitRate;
        }

        public int getWidthAlignment() {
            return widthAlignment;
        }

        public void setWidthAlignment(int widthAlignment) {
            this.widthAlignment = widthAlignment;
        }

        public int getHeightAlignment() {
            return heightAlignment;
        }

        public void setHeightAlignment(int heightAlignment) {
            this.heightAlignment = heightAlignment;
        }

        public String getSupportedFrameRates() {
            return supportedFrameRates;
        }

        public void setSupportedFrameRates(String supportedFrameRates) {
            this.supportedFrameRates = supportedFrameRates;
        }

        public String getSupportedWidths() {
            return supportedWidths;
        }

        public void setSupportedWidths(String supportedWidths) {
            this.supportedWidths = supportedWidths;
        }

        public String getSupportedHeights() {
            return supportedHeights;
        }

        public void setSupportedHeights(String supportedHeights) {
            this.supportedHeights = supportedHeights;
        }

        public String getSupportedPerformancePoints() {
            return supportedPerformancePoints;
        }

        public void setSupportedPerformancePoints(String supportedPerformancePoints) {
            this.supportedPerformancePoints = supportedPerformancePoints;
        }

        @Override
        public String toString() {
            return "VideoCodecInfo{" +
                    "bitRate='" + bitRate + '\'' +
                    ",\nwidthAlignment=" + widthAlignment +
                    ",\nheightAlignment=" + heightAlignment +
                    ",\nsupportedFrameRates='" + supportedFrameRates + '\'' +
                    ",\nsupportedWidths='" + supportedWidths + '\'' +
                    ",\nsupportedHeights='" + supportedHeights + '\'' +
                    '}';
        }
    }

    public class EncoderInfo {
        String complexity;
        String quality;

        public EncoderInfo(String complexity, String quality) {
            this.complexity = complexity;
            this.quality = quality;
        }

        public String getComplexity() {
            return complexity;
        }

        public void setComplexity(String complexity) {
            this.complexity = complexity;
        }

        public String getQuality() {
            return quality;
        }

        public void setQuality(String quality) {
            this.quality = quality;
        }

        @Override
        public String toString() {
            return "EncoderInfo{" +
                    "complexity='" + complexity + '\'' +
                    ", \nquality='" + quality + '\'' +
                    '}';
        }
    }

}
