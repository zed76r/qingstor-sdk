package com.zedcn.qingstor.conn;

import com.zedcn.qingstor.elements.QingStorBucket;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

/**
 * 青云对象存储连接
 * Created by Zed on 2016/3/19.
 */
public class QingStorConnection {
    private HttpClient httpClient;
    private QingStorBucket qingStorBucket;
    private String baseUrl;

    protected QingStorConnection() {
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
        connection.baseUrl = "http://" + qingStorBucket.getBucket() + "." + qingStorBucket.getZone() + ".qingstor.com";
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

    public boolean isBucketExist() {
        HttpHead head = new HttpHead(baseUrl);
        long reqTime = System.currentTimeMillis();
        head.addHeader("Date", SignBuilder.getGMTTime(reqTime));
        head.addHeader("Authorization", SignBuilder.newSign(qingStorBucket).setMethod("HEAD").setResourceName("/" + qingStorBucket.getBucket()).setTimeInMillins(reqTime).build());
        try {
            HttpResponse response = httpClient.execute(head);
            return response.getStatusLine().getStatusCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }
}
