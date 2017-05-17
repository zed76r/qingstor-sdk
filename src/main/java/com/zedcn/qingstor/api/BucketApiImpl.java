package com.zedcn.qingstor.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zedcn.qingstor.elements.QingCloudAccess;
import com.zedcn.qingstor.elements.QingStorBucket;
import com.zedcn.qingstor.excption.RequestExcption;
import com.zedcn.qingstor.excption.UnauthorizedExcption;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.zedcn.qingstor.api.ApiUtils.*;
import static com.zedcn.qingstor.api.SignBuilder.getGMTTime;
import static com.zedcn.qingstor.api.SignBuilder.newSign;

class BucketApiImpl implements BucketApi {
    private final QingCloudAccess access;
    private final String baseUrl;

    BucketApiImpl(QingCloudAccess access) {
        this.access = access;
        baseUrl = "https://" + access.getLocation() + ".qingstor.com/";
    }

    @Override
    public List<QingStorBucket> listBucket() {
        long now = System.currentTimeMillis();
        Request request = new Request.Builder()
                .url("https://qingstor.com")
                .get()
                .addHeader("Date", getGMTTime(now))
                .addHeader("Authorization",
                        newSign()
                                .setMethod(GET)
                                .setTimeInMillis(now)
                                .setResourceName("/")
                                .setAccessKey(access.getAccessKey())
                                .setAccessSecret(access.getAccessSecret())
                                .build())
                .build();
        try {
            Response response = getClient().newCall(request).execute();
            ResponseBody body = response.body();
            if (Objects.isNull(body)) return Collections.emptyList();
            String result = body.string();
            switch (response.code()) {
                case 200:
                    JsonObject object = new Gson().fromJson(result, JsonObject.class);
                    int count = object.get("count").getAsInt();
                    if (count == 0) return Collections.emptyList();
                    return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                            .create().fromJson(object.get("buckets"), new TypeToken<ArrayList<QingStorBucket>>() {
                            }.getType());
                case 403:
                    throw new UnauthorizedExcption();
            }
            return Collections.emptyList();
        } catch (IOException e) {
            throw new RequestExcption(e);
        }
    }

    @Override
    public void createBucket(String bucketName) {
        long now = System.currentTimeMillis();
        Request request = new Request.Builder()
                .url(baseUrl + bucketName)
                .put(RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), ""))
                .addHeader("Date", getGMTTime(now))
                .addHeader("Authorization", newSign()
                        .setAccessKey(access.getAccessKey())
                        .setAccessSecret(access.getAccessSecret())
                        .setMethod(PUT)
                        .setTimeInMillis(now)
                        .setResourceName("/" + bucketName)
                        .setContentType("text/plain; charset=utf-8")
                        .build())
                .build();
        requestAnyway(request);
    }

    @Override
    public void deleteBucket(String bucketName) {
        long now = System.currentTimeMillis();
        Request request = new Request.Builder()
                .url(baseUrl + bucketName)
                .delete()
                .addHeader("Date", getGMTTime(now))
                .addHeader("Authorization", newSign()
                        .setAccessKey(access.getAccessKey())
                        .setAccessSecret(access.getAccessSecret())
                        .setMethod(DELETE)
                        .setTimeInMillis(now)
                        .setResourceName("/" + bucketName)
                        .build()
                )
                .build();
        requestAnyway(request);
    }

    @Override
    public boolean exists(String bucketName) {
        long now = System.currentTimeMillis();
        Request request = new Request.Builder()
                .url(baseUrl + bucketName)
                .head()
                .addHeader("Date", getGMTTime(now))
                .addHeader("Authorization",
                        newSign(access).setMethod(HEAD)
                                .setResourceName("/" + bucketName)
                                .setTimeInMillis(now).build()
                ).build();
        try {
            Response response = getClient().newCall(request).execute();
            return response.code() == 200;
        } catch (IOException e) {
            throw new RequestExcption(e);
        }
    }
}
