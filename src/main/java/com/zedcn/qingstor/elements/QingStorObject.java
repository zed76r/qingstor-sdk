package com.zedcn.qingstor.elements;


import java.io.*;

/**
 * 青云对象存储Object实例
 * Created by Zed on 2016/3/19.
 */
@SuppressWarnings("unused")
public class QingStorObject {
    /**
     * 对象Key
     */
    private String key;
    /**
     * 对象类型，默认为application/oct-stream
     */
    private String contentType;
    /**
     * 对象长度
     */
    private long contentLength;
    /**
     * 对象MD5校验码
     */
    private String contentMD5;
    /**
     * 对象实体
     */
    private InputStream content;

    public QingStorObject() {
        setContentType(ContentType.DEFAULT_BINARY);
    }

    public String getKey() {
        return key;
    }

    public QingStorObject setKey(String key) {
        this.key = key;
        return this;
    }

    public InputStream getContent() {
        return content;
    }

    public QingStorObject setContent(InputStream content) {
        this.content = content;
        return this;
    }

    public long getContentLength() {
        return contentLength;
    }

    public QingStorObject setContentLength(long contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    /**
     * 自动获取二进制内容大小
     */
    public QingStorObject autoContentLength() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[128];
        try {
            while (getContent().read(bytes) != -1) {
                byteArrayOutputStream.write(bytes, 0, bytes.length);
            }
            setContentLength(byteArrayOutputStream.size());
            getContent().close();
            setContent(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
//            byteArrayOutputStream.reset();
//            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getContentMD5() {
        return contentMD5;
    }

    public QingStorObject setContentMD5(String contentMD5) {
        this.contentMD5 = contentMD5;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public QingStorObject setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * 青云对象类型
     */
    public static class ContentType {
        public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
        public static final String APPLICATION_JSON = "application/json";
        public static final String TEXT_PLAIN = "text/plain";
        public static final String IMAGE_ALL = "image/*";
        public static final String IMAGE_JPG = "image/jpg";
        public static final String IMAGE_JPEG = "image/jpeg";
        public static final String IMAGE_PNG = "image/png";
        public static final String IMAGE_GIF = "image/gif";


        public static final String DEFAULT_BINARY = APPLICATION_OCTET_STREAM;
        public static final String DEFAULT = TEXT_PLAIN;
    }
}
