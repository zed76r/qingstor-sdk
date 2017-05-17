package com.zedcn.qingstor.api;

import com.zedcn.qingstor.elements.QingStorBucket;
import com.zedcn.qingstor.elements.QingStorObject;
import com.zedcn.qingstor.excption.ObjectPutExcption;
import com.zedcn.qingstor.excption.RequestExcption;
import com.zedcn.qingstor.excption.UnauthorizedExcption;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

import static com.zedcn.qingstor.api.ApiUtils.*;
import static com.zedcn.qingstor.api.SignBuilder.getGMTTime;
import static com.zedcn.qingstor.api.SignBuilder.newSign;

/**
 * 青云对象API实现
 * Created by Zed on 2016/8/16.
 */
class ObjectApiImpl implements ObjectApi {
    private final QingStorBucket bucket;
    private final String baseUrl;

    ObjectApiImpl(QingStorBucket bucket) {
        Assert.notNull(bucket);
        this.bucket = bucket;
        baseUrl = "https://" + bucket.getName() + "." + bucket.getLocation() + ".qingstor.com/";
    }

    @Override
    public QingStorObject get(String objectName) {
        OkHttpClient okHttpClient = getClient();
        long now = System.currentTimeMillis();
        Request request = new Request.Builder()
                .url(baseUrl + objectName)
                .get()
                .addHeader("Date", getGMTTime(now))
                .addHeader("Authorization",
                        newSign(bucket).setMethod(GET)
                                .setResourceName("/" + bucket.getName() + "/" + objectName)
                                .setTimeInMillis(now).build()
                ).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return ApiUtils.buildObject(objectName, response.body());
            }
            return null;
        } catch (IOException e) {
            throw new RequestExcption();
        }
    }

    @Override
    public void create(QingStorObject object) {
        OkHttpClient client = getClient();
        long now = System.currentTimeMillis();
        Request request = new Request.Builder()
                .url(baseUrl + object.getKey())
                .put(
                        Objects.nonNull(object.getContent()) ?
                                streamBody(object.getContentLength(), object.getContent(), MediaType.parse(object.getContentType()))
                                : RequestBody.create(MediaType.parse(object.getContentType()), object.getContentBinary())
                )
                .addHeader("Date", getGMTTime(now))
                .addHeader("Authorization",
                        newSign(bucket).setMethod(PUT)
                                .setContentType(object.getContentType())
                                .setResourceName("/" + bucket.getName() + "/" + object.getKey())
                                .setTimeInMillis(now).build()
                ).build();
        try {
            Response response = client.newCall(request).execute();
            switch (response.code()) {
                case 201:
                case 200:
                    return;
                case 403:
                    throw new UnauthorizedExcption();
                default:
                    throw new ObjectPutExcption(response.code());
            }
        } catch (IOException e) {
            throw new RequestExcption();
        }
    }


    @Override
    public void copy(String source, String target) {
        long now = System.currentTimeMillis();

        Request request = new Request.Builder()
                .url(baseUrl + target)
                .put(RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), ""))
                .addHeader("X-QS-Copy-Source", "/" + bucket.getName() + "/" + source)
                .addHeader("Date", getGMTTime(now))
                .addHeader("Authorization", newSign()
                        .setAccessKey(bucket.getAccessKey())
                        .setAccessSecret(bucket.getAccessSecret())
                        .setMethod(PUT)
                        .setTimeInMillis(now)
                        .setResourceName("/" + bucket.getName() + "/" + target)
                        .setContentType("text/plain; charset=utf-8")
                        .addHeaders("X-QS-Copy-Source", "/" + bucket.getName() + "/" + source)
                        .build())
                .build();
        requestAnyway(request);
    }

    @Override
    public void delete(String objectName) {
        long now = System.currentTimeMillis();
        Request request = new Request.Builder()
                .url(baseUrl + objectName)
                .delete()
                .addHeader("Date", getGMTTime(now))
                .addHeader("Authorization", newSign()
                        .setAccessKey(bucket.getAccessKey())
                        .setAccessSecret(bucket.getAccessSecret())
                        .setMethod(DELETE)
                        .setTimeInMillis(now)
                        .setResourceName("/" + bucket.getName() + "/" + objectName)
                        .build()
                )
                .build();
        requestAnyway(request);

    }

    private void requestAnyway(Request request) {
        OkHttpClient client = getClient();
        try {
            Response response = client.newCall(request).execute();
            switch (response.code()) {
                case 401:
                case 403:
                    throw new UnauthorizedExcption();
            }
        } catch (IOException e) {
            throw new RequestExcption();
        }
    }

    @Override
    public QingStorObject exists(String objectName) {
        OkHttpClient okHttpClient = getClient();
        long now = System.currentTimeMillis();
        Request request = new Request.Builder()
                .url(baseUrl + objectName)
                .head()
                .addHeader("Date", getGMTTime(now))
                .addHeader("Authorization",
                        newSign(bucket).setMethod(HEAD)
                                .setResourceName("/" + bucket.getName() + "/" + objectName)
                                .setTimeInMillis(now).build()
                ).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                QingStorObject object = new QingStorObject();
                object.setContentType(response.header("Content-Type"));
                String contentLenght = response.header("Content-Length");
                object.setContentLength(Long.parseLong(Objects.nonNull(contentLenght) ? contentLenght : "0"));
                object.setKey(objectName);
                return object;
            }
            return null;
        } catch (IOException e) {
            throw new RequestExcption();
        }
    }

}
