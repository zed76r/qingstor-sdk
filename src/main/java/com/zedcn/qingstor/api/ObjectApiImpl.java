package com.zedcn.qingstor.api;

import com.zedcn.qingstor.elements.QingStorBucket;
import com.zedcn.qingstor.elements.QingStorObject;
import com.zedcn.qingstor.excption.ObjectPutExcption;
import com.zedcn.qingstor.excption.RequestExcption;
import com.zedcn.qingstor.excption.UnauthorizedExcption;
import okhttp3.*;

import java.io.IOException;

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
        baseUrl = "http://" + bucket.getName() + "." + bucket.getLocation() + ".qingstor.com/";
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
            if (response.code() == 200) {
                return ApiUtils.buildObject(objectName, response.body());
            } else {
                System.out.println(response.body().string());
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
                .put(streamBody(object.getContent(), MediaType.parse(object.getContentType())))
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
                    System.out.println(response.body().string());
                    throw new UnauthorizedExcption();
                default:
                    System.out.println(response.body().string());
                    throw new ObjectPutExcption(response.code());
            }
        } catch (IOException e) {
            throw new RequestExcption();
        }
    }


    @Override
    public void copy(String source, String target) {
        OkHttpClient client = getClient();
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
        try {
            Response response = client.newCall(request).execute();
            switch (response.code()) {
                case 201:
                    break;
                case 401:
                case 403:
                    System.out.println(response.body().string());
                    throw new UnauthorizedExcption();
            }
        } catch (IOException e) {
            throw new RequestExcption();
        }
    }

    @Override
    public void delete(String objectName) {
        OkHttpClient client = getClient();
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
        try {
            Response response = client.newCall(request).execute();
            switch (response.code()){
                case 401:
                case 403:
                    System.out.println(response.body().string());
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
            if (response.code() == 200) {
                QingStorObject object = new QingStorObject();
                object.setContentType(response.header("Content-Type"));
                object.setContentLength(Long.parseLong(response.header("Content-Length")));
                object.setKey(objectName);
                return object;
            } else {
                System.out.println(response.body().string());
            }
            return null;
        } catch (IOException e) {
            throw new RequestExcption();
        }
    }

}
