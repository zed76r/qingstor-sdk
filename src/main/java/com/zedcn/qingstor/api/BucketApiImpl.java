package com.zedcn.qingstor.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zedcn.qingstor.elements.QingCloudAccess;
import com.zedcn.qingstor.elements.QingStorBucket;
import com.zedcn.qingstor.elements.QingStorObject;
import com.zedcn.qingstor.excption.RequestExcption;
import com.zedcn.qingstor.excption.UnauthorizedExcption;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.zedcn.qingstor.api.ApiUtils.*;
import static com.zedcn.qingstor.api.SignBuilder.getGMTTime;
import static com.zedcn.qingstor.api.SignBuilder.newSign;

class BucketApiImpl implements BucketApi {
    private final QingCloudAccess access;
    private final String baseUrl;

    BucketApiImpl(QingCloudAccess access) {
        this.access = access;
        baseUrl = "http://" + access.getLocation() + ".qingstor.com/";
    }

    @Override
    public List<QingStorBucket> listBucket() {
        long now = System.currentTimeMillis();
        Request request = new Request.Builder()
                .url("http://qingstor.com")
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
            String result = response.body().string();
            if (response.code() == 200) {
                JsonObject object = new Gson().fromJson(result, JsonObject.class);
                int count = object.get("count").getAsInt();
                if (count == 0) return Collections.emptyList();
                return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                        .create().fromJson(object.get("buckets"), new TypeToken<ArrayList<QingStorBucket>>() {
                        }.getType());
            }
            switch (response.code()) {
                case 403:
                    System.out.println(result);
                    throw new UnauthorizedExcption();
                default:
                    System.out.println(result);
                    break;
            }
            return Collections.emptyList();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RequestExcption();
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
        try {
            Response response = getClient().newCall(request).execute();
            switch (response.code()) {
                case 200:
                case 201:
                    //ok
                    break;
                case 401:
                case 403:
                    System.out.println(response.body().string());
                    throw new UnauthorizedExcption();
                default:
                    System.out.println(response.body().string());
                    break;
            }
        } catch (IOException e) {
            throw new RequestExcption();
        }
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
        try {
            Response response = getClient().newCall(request).execute();
            switch (response.code()) {
                case 200:
                case 201:
                    break;
                case 401:
                case 403:
                    System.out.println(response.body().string());
                    throw new UnauthorizedExcption();
                default:
                    System.out.println(response.body().string());
                    break;
            }
        } catch (IOException e) {
            throw new RequestExcption();
        }
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
            throw new RequestExcption();
        }
    }
}
