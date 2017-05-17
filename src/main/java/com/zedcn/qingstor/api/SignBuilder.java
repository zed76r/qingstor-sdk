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
@SuppressWarnings("WeakerAccess")
class SignBuilder {
    private String method;
    private String contentMD5;
    private String contentType;
    private long timeInMillis;
    private String resourceName;
    private String accessKey;
    private String accessSecret;
    private HashMap<String, String> params = new HashMap<>();
    private HashMap<String, String> headers = new HashMap<>();

    static SignBuilder newSign() {
        return newSign(null);
    }

    static SignBuilder newSign(QingCloudAccess qingCloudAccess) {
        SignBuilder signBuilder = new SignBuilder();
        if (qingCloudAccess != null)
            signBuilder.setAccessKey(qingCloudAccess.getAccessKey())
                    .setAccessSecret(qingCloudAccess.getAccessSecret());
        return signBuilder;
    }

    static SignBuilder newSign(QingCloudAccess access, QingStorObject object) {
        return newSign(access).setContentType(object.getContentType()).setContentMD5(object.getContentMD5());
    }

    static String getGMTTime(long timeInMillis) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date(timeInMillis));
    }

    SignBuilder setAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    SignBuilder setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
        return this;
    }

    SignBuilder setContentMD5(String contentMD5) {
        this.contentMD5 = contentMD5;
        return this;
    }

    SignBuilder setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    SignBuilder setMethod(String method) {
        this.method = method;
        return this;
    }

    SignBuilder setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    SignBuilder setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
        return this;
    }

    SignBuilder setParams(HashMap<String, String> params) {
        this.params.putAll(params);
        return this;
    }

    SignBuilder addParam(String key, String value) {
        params.put(key, value);
        return this;
    }

    SignBuilder setHeaders(HashMap<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    @SuppressWarnings("SameParameterValue")
    SignBuilder addHeaders(String key, String value) {
        headers.put(key, value);
        return this;
    }

    String build() {
        String toSign = method + "\n";
        toSign += (contentMD5 == null ? "" : contentMD5) + "\n";
        toSign += (contentType == null ? "" : contentType) + "\n";
        toSign += getGMTTime(timeInMillis) + "\n";
        if (!ApiUtils.isEmpty(headers)) {
            StringBuilder headerSB = new StringBuilder();
            headers.forEach((k, v) -> {
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
            if (paramSB.length() > 1) {
                paramSB.deleteCharAt(paramSB.length() - 1);
                toSign += "?" + paramSB.toString();
            }
        }
        SecretKey secretKey = new SecretKeySpec(accessSecret.getBytes(), "HmacSHA256");
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            sha256_HMAC.init(secretKey);
            return "QS-HMAC-SHA256 " + accessKey + ":" + Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(toSign.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new SignExption();
        }
    }
}
