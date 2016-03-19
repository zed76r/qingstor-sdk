package com.zedcn.qingstor.elements;

/**
 * 登录青云基本信息
 * Created by Zed on 2016/3/19.
 */
@SuppressWarnings("unused")
public class QingCloudAccess {
    /**
     * 青云访问服务所在区域。默认为PEK3A区。
     */
    private String location;
    /**
     * 用于访问青云API的AccessKeyID
     */
    private String accessKey;
    /**
     * 用于访问青云API的SecretAccessKey
     */
    private String accessSecret;

    public QingCloudAccess(){
        setLocation("pek3a");//默认区域
    }

    public String getAccessKey() {
        return accessKey;
    }

    public QingCloudAccess setAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public QingCloudAccess setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public QingCloudAccess setLocation(String location) {
        this.location = location;
        return this;
    }
}
