package com.zedcn.qingstor.conn;

import com.zedcn.qingstor.elements.QingCloudAccess;
import com.zedcn.qingstor.elements.QingStorObject;
import com.zedcn.qingstor.excption.SignExption;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 签名构造器
 * Created by mrfen on 2016/3/19.
 */
@SuppressWarnings("unused")
public class SignBuilder {
    private String method;
    private String contentMD5;
    private String contentType;
    private long timeInMillins;
    private String resourceName;
    private String accessKey;
    private String accessSecret;
    private List<NameValuePair> params;

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

    public static String getGMTTime(long timeInMillins) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date(timeInMillins));
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

    public SignBuilder setTimeInMillins(long timeInMillins) {
        this.timeInMillins = timeInMillins;
        return this;
    }

    public SignBuilder setParams(List<NameValuePair> params) {
        this.params = params;
        return this;
    }

    public String build() {
        String toSign = method + "\n";
        toSign += (contentMD5 == null ? "" : contentMD5) + "\n";
        toSign += (contentType == null ? "" : contentType) + "\n";
        toSign += getGMTTime(timeInMillins) + "\n";
        toSign += resourceName;
        if (params != null && params.size() > 0) {
            String paramStr = URLEncodedUtils.format(params, Charset.forName("UTF-8"));
            if (!paramStr.isEmpty()) {
                if ('=' == (paramStr.charAt(paramStr.length() - 1))) {
                    paramStr = paramStr.substring(0, paramStr.length() - 1);
                }
                toSign += "?" + paramStr;
            }
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
