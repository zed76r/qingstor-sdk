package com.zedcn.qingstor.conn;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zedcn.qingstor.elements.QingCloudAccess;
import com.zedcn.qingstor.elements.QingStorBucket;
import com.zedcn.qingstor.elements.QingStorObject;
import com.zedcn.qingstor.excption.ObjectPutExcption;
import com.zedcn.qingstor.excption.RequestExcption;
import com.zedcn.qingstor.excption.UnauthorizedExcption;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 青云对象存储连接
 * Created by Zed on 2016/3/19.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class QingStorConnection {
    private static final String SCHEME = "http://";
    private HttpClient httpClient;
    private QingStorBucket qingStorBucket;
    private String baseUrl;

    private QingStorConnection() {
    }

    /**
     * 创建一个对象存储连接
     *
     * @param qingStorBucket    对象存储Bucket
     * @param httpClientBuilder HttpClient自定义构造器
     * @return 连接实例
     */
    public static QingStorConnection create(QingStorBucket qingStorBucket, HttpClientBuilder httpClientBuilder) {
        QingStorConnection connection = new QingStorConnection();
        connection.qingStorBucket = qingStorBucket;
        connection.httpClient = httpClientBuilder.build();
        connection.baseUrl = SCHEME + qingStorBucket.getName() + "." + qingStorBucket.getLocation() + ".qingstor.com";
        return connection;
    }

    /**
     * 创建一个对象存储连接，使用默认的连接构造
     *
     * @param qingStorBucket 对象存储Bucket
     * @return 连接实例
     */
    public static QingStorConnection create(QingStorBucket qingStorBucket) {
        return create(qingStorBucket, HttpClientBuilder.create());
    }

    /**
     * 获取所有Bucket
     * @param qingCloudAccess 青云API密钥
     * @return Bucket集合
     */
    public static List<QingStorBucket> getAllBuckets(QingCloudAccess qingCloudAccess) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(SCHEME + "qingstor.com");
        long reqTime = System.currentTimeMillis();
        get.addHeader("Date", SignBuilder.getGMTTime(reqTime));
        get.addHeader("Authorization", SignBuilder.newSign(qingCloudAccess).setMethod(get.getMethod()).setResourceName("/").setTimeInMillins(reqTime).build());
        try {
            HttpResponse response = client.execute(get);
            int httpcode = response.getStatusLine().getStatusCode();
            if (httpcode == 200) {
                JsonObject jsonObject = new Gson().fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);
                if (jsonObject.getAsJsonPrimitive("count").getAsInt() != 0) {
                    JsonArray array = jsonObject.getAsJsonArray("buckets");
                    List<QingStorBucket> buckets = new ArrayList<>();
                    String qcaStr = new Gson().toJson(qingCloudAccess);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject bucketObject = array.get(i).getAsJsonObject();
                        QingStorBucket bucket = new Gson().fromJson(qcaStr, QingStorBucket.class);
                        bucket.setName(bucketObject.getAsJsonPrimitive("name").getAsString());
                        try {
                            bucket.setCreated(simpleDateFormat.parse(bucketObject.getAsJsonPrimitive("created").getAsString()));
                        } catch (ParseException e) {
                            bucket.setCreated(new Date());
                        }
                        buckets.add(bucket);
                    }
                    return buckets;
                }
                return null;
            } else switch (httpcode) {
                case 403:
                    System.out.println(EntityUtils.toString(response.getEntity()));
                    throw new UnauthorizedExcption();
                default:
                    throw new RequestExcption();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            HttpClientUtils.closeQuietly(client);
        }
    }

    /**
     * 验证Bucket是否存在
     *
     * @return 如果存在则返回@code=true
     */
    public boolean isBucketExist() {
        HttpHead head = new HttpHead(baseUrl);
        long reqTime = System.currentTimeMillis();
        head.addHeader("Date", SignBuilder.getGMTTime(reqTime));
        head.addHeader("Authorization", SignBuilder.newSign(qingStorBucket).setMethod(head.getMethod()).setResourceName("/" + qingStorBucket.getName()).setTimeInMillins(reqTime).build());
        try {
            HttpResponse response = httpClient.execute(head);
            return response.getStatusLine().getStatusCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 统计当前Bucket状态
     *
     * @return Bucket对象
     */
    public QingStorBucket statistics() {
        HttpGet get = new HttpGet(baseUrl + "/?stats");
        long reqTime = System.currentTimeMillis();
        get.addHeader("Date", SignBuilder.getGMTTime(reqTime));
        get.addHeader("Authorization", SignBuilder.newSign(qingStorBucket).setMethod(get.getMethod()).setParams(Collections.singletonList(new BasicNameValuePair("stats", ""))).setResourceName("/" + qingStorBucket.getName()).setTimeInMillins(reqTime).build());
        try {
            HttpResponse response = httpClient.execute(get);
            switch (response.getStatusLine().getStatusCode()) {
                case 200:
                    String result = EntityUtils.toString(response.getEntity());
                    QingStorBucket qingStorBucket = new Gson().fromJson(result, QingStorBucket.class);
                    qingStorBucket.setAccessSecret(this.qingStorBucket.getAccessSecret());
                    qingStorBucket.setAccessKey(this.qingStorBucket.getAccessKey());
                    return this.qingStorBucket = qingStorBucket;
                case 401:
                    throw new UnauthorizedExcption();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return qingStorBucket;
    }

    /**
     * 获取一个对象，如果对象不存在则返回@Code=null
     *
     * @param key 对象的Key值
     * @return 对象实例
     */
    public QingStorObject getObject(String key) {
        long reqTime = System.currentTimeMillis();
        HttpGet get = new HttpGet(baseUrl + "/" + key);
        get.addHeader("Date", SignBuilder.getGMTTime(reqTime));
        get.addHeader("Authorization", SignBuilder.newSign(qingStorBucket).setMethod(get.getMethod()).setResourceName("/" + qingStorBucket.getName() + "/" + key).setTimeInMillins(reqTime).build());
        try {
            HttpResponse response = httpClient.execute(get);
            switch (response.getStatusLine().getStatusCode()) {
                case 200: {
                    Header type = response.getFirstHeader("Content-Type");
                    Header length = response.getFirstHeader("Content-Length");
                    return new QingStorObject()
                            .setKey(key)
                            .setContent(response.getEntity().getContent())
                            .setContentType(type != null ? type.getValue() : QingStorObject.ContentType.DEFAULT_BINARY)
                            .setContentLength(length != null ? Long.parseLong(length.getValue()) : 0);
                }
                case 401:
                    throw new UnauthorizedExcption();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存入一个对象
     *
     * @param object 对象实例
     */
    public void putObject(QingStorObject object) {
        long reqTime = System.currentTimeMillis();
        HttpPut put = new HttpPut(baseUrl + "/" + object.getKey());
        put.addHeader("Content-Lenght", String.valueOf(object.getContentLength()));
        if (!isEmpty(object.getContentMD5())) {
            put.addHeader("Content-MD5", object.getContentMD5());
        }
        if (!isEmpty(object.getContentType())) {
            put.addHeader("Content-Type", object.getContentType());
        }
        put.addHeader("Date", SignBuilder.getGMTTime(reqTime));
        put.addHeader("Authorization", SignBuilder.newSign(qingStorBucket, object).setMethod(put.getMethod()).setResourceName("/" + qingStorBucket.getName() + "/" + object.getKey()).setTimeInMillins(reqTime).build());
        BasicHttpEntity httpEntity = new BasicHttpEntity();
        httpEntity.setContent(object.getContent());
        put.setEntity(httpEntity);
        try {
            HttpResponse response = httpClient.execute(put);
            int code;
            switch (code = response.getStatusLine().getStatusCode()) {
                case 200:
                case 201:
                    //Do nothing...
                    break;
                case 401:
                    throw new UnauthorizedExcption();
                default:
                    throw new ObjectPutExcption(code);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一个对象
     *
     * @param key 对象Key值
     */
    public void deleteObject(String key) {
        long reqTime = System.currentTimeMillis();
        HttpDelete delete = new HttpDelete(baseUrl + "/" + key);
        delete.addHeader("Date", SignBuilder.getGMTTime(reqTime));
        delete.addHeader("Authorization", SignBuilder.newSign(qingStorBucket).setMethod(delete.getMethod()).setResourceName("/" + qingStorBucket.getName() + "/" + key).setTimeInMillins(reqTime).build());
        try {
            HttpResponse httpResponse = httpClient.execute(delete);
            int code = httpResponse.getStatusLine().getStatusCode();
            if (code >= 200 && code < 300) return;
            switch (code) {
                case 401:
                    throw new UnauthorizedExcption();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭此链接
     */
    public void close() {
        HttpClientUtils.closeQuietly(httpClient);
    }

    private boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
