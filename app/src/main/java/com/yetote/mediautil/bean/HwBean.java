package com.yetote.mediautil.bean;

public class HwBean {
    private String codecName;
    private String canonicalName;
    private String supportedTypes;
    private String isAlias;
    private String isEncoder;
    private String isHardwareAccelerated;
    private String isSoftwareOnly;
    private String isVendor;

    public HwBean(String codecName, String canonicalName, String supportedTypes, String isAlias, String isEncoder, String isHardwareAccelerated, String isSoftwareOnly, String isVendor) {
        this.codecName = codecName;
        this.canonicalName = canonicalName;
        this.supportedTypes = supportedTypes;
        this.isAlias = isAlias;
        this.isEncoder = isEncoder;
        this.isHardwareAccelerated = isHardwareAccelerated;
        this.isSoftwareOnly = isSoftwareOnly;
        this.isVendor = isVendor;
    }

    public String getCodecName() {
        return codecName;
    }

    public void setCodecName(String codecName) {
        this.codecName = codecName;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public String getSupportedTypes() {
        return supportedTypes;
    }

    public void setSupportedTypes(String supportedTypes) {
        this.supportedTypes = supportedTypes;
    }

    public String getIsAlias() {
        return isAlias;
    }

    public void setIsAlias(String isAlias) {
        this.isAlias = isAlias;
    }

    public String getIsEncoder() {
        return isEncoder;
    }

    public void setIsEncoder(String isEncoder) {
        this.isEncoder = isEncoder;
    }

    public String getIsHardwareAccelerated() {
        return isHardwareAccelerated;
    }

    public void setIsHardwareAccelerated(String isHardwareAccelerated) {
        this.isHardwareAccelerated = isHardwareAccelerated;
    }

    public String getIsSoftwareOnly() {
        return isSoftwareOnly;
    }

    public void setIsSoftwareOnly(String isSoftwareOnly) {
        this.isSoftwareOnly = isSoftwareOnly;
    }

    public String getIsVendor() {
        return isVendor;
    }

    public void setIsVendor(String isVendor) {
        this.isVendor = isVendor;
    }

    @Override
    public String toString() {
        return "编解码器名称:" + codecName + "\n"
                + "检索基础编解码器名称:" + canonicalName + "\n"
                + "支持的媒体类型:" + supportedTypes + "\n"
                + "是否为别名:" + isAlias + "\n"
                + "是否为编码器:" + isEncoder + "\n"
                + "是否使用硬件加速:" + isHardwareAccelerated + "\n"
                + "是否为软编解码器:" + isSoftwareOnly + "\n"
                + "是否由供应商提供 : " + isVendor + "\n";
    }
}
