package com.zedcn.qingstor.elements;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 青云对象存储Object实例
 * Created by Zed on 2016/3/19.
 */
@SuppressWarnings({"unused", "UnusedReturnValue", "WeakerAccess"})
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
    /**
     * 对象的二进制数组
     */
    private byte[] contentBinary;

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
     *
     * @return 该实例
     */
    public QingStorObject autoContentLength() {
        if (Objects.nonNull(getContent())) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[128];
            try {
                while (getContent().read(bytes) != -1) {
                    byteArrayOutputStream.write(bytes, 0, bytes.length);
                }
                setContentLength(byteArrayOutputStream.size());
                getContent().close();
                setContent(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (Objects.nonNull(getContentBinary())) {
            setContentLength(getContentBinary().length);
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

    public byte[] getContentBinary() {
        return contentBinary;
    }

    public QingStorObject setContentBinary(byte[] contentBinary) {
        this.contentBinary = contentBinary;
        return this;
    }

    /**
     * 青云对象类型
     */
    @SuppressWarnings("WeakerAccess")
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
