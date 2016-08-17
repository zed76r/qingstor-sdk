package com.zedcn.qingstor.api;

import com.zedcn.qingstor.elements.QingCloudAccess;
import com.zedcn.qingstor.elements.QingStorObject;
import com.zedcn.qingstor.excption.SignExption;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 签名构造器
 * Created by Zed on 2016/3/19.
 */
public class SignBuilder {
    private String method;
    private String contentMD5;
    private String contentType;
    private long timeInMillis;
    private String resourceName;
    private String accessKey;
    private String accessSecret;
    private HashMap<String, String> params = new HashMap<>();
    private HashMap<String, String> headers = new HashMap<>();

    public static SignBuilder newSign() {
        return newSign(null);
    }

    public static SignBuilder newSign(QingCloudAccess qingCloudAccess) {
        SignBuilder signBuilder = new SignBuilder();
        if (qingCloudAccess != null)
            signBuilder.setAccessKey(qingCloudAccess.getAccessKey())
                    .setAccessSecret(qingCloudAccess.getAccessSecret());
        return signBuilder;
    }

    public static SignBuilder newSign(QingCloudAccess access, QingStorObject object) {
        return newSign(access).setContentType(object.getContentType()).setContentMD5(object.getContentMD5());
    }

    public static String getGMTTime(long timeInMillis) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date(timeInMillis));
    }

    public SignBuilder setAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public SignBuilder setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
        return this;
    }

    public SignBuilder setContentMD5(String contentMD5) {
        this.contentMD5 = contentMD5;
        return this;
    }

    public SignBuilder setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public SignBuilder setMethod(String method) {
        this.method = method;
        return this;
    }

    public SignBuilder setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public SignBuilder setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
        return this;
    }

    public SignBuilder setParams(HashMap<String, String> params) {
        this.params.putAll(params);
        return this;
    }

    public SignBuilder addParam(String key, String value) {
        params.put(key, value);
        return this;
    }

    public SignBuilder setHeaders(HashMap<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public SignBuilder addHeaders(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public String build() {
        String toSign = method + "\n";
        toSign += (contentMD5 == null ? "" : contentMD5) + "\n";
        toSign += (contentType == null ? "" : contentType) + "\n";
        toSign += getGMTTime(timeInMillis) + "\n";
        if (!ApiUtils.isEmpty(headers)) {
            StringBuilder headerSB = new StringBuilder();
            headers.forEach((k,v)->{
                if (ApiUtils.isEmpty(v)) return;
                headerSB.append(k.toLowerCase())
                        .append(":")
                        .append(v)
                        .append("\n");
            });
            toSign += headerSB.toString();
        }
        toSign += resourceName;
        if (!ApiUtils.isEmpty(params)) {
            StringBuilder paramSB = new StringBuilder();
            params.forEach((k, v) -> {
                if (ApiUtils.isEmpty(v)) return;
                paramSB.append(k)
                        .append("=")
                        .append(v)
                        .append("&");
            });
            paramSB.deleteCharAt(paramSB.length() - 1);
            toSign += "?" + paramSB.toString();
        }
        SecretKey secretKey = new SecretKeySpec(accessSecret.getBytes(), "HmacSHA256");
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            sha256_HMAC.init(secretKey);
            System.out.println(toSign);
            return "QS-HMAC-SHA256 " + accessKey + ":" + Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(toSign.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new SignExption();
        }
    }
}
