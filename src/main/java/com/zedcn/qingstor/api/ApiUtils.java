package com.zedcn.qingstor.api;

import com.zedcn.qingstor.elements.QingStorObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * 青云对象Api工具类
 * Created by Zed on 2016/8/16.
 */
final class ApiUtils {

    static final String GET = "GET";
    static final String PUT = "PUT";
    static final String DELETE = "DELETE";
    static final String HEAD = "HEAD";

    static QingStorObject buildObject(String key, ResponseBody body) {
        QingStorObject object = new QingStorObject();
        object.setKey(key);
        object.setContentType(body.contentType().type());
        object.setContentLength(body.contentLength());
        object.setContent(body.byteStream());
        return object;
    }

    static OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .build();
    }

    static RequestBody streamBody(long length, InputStream inputStream, MediaType mediaType) {
        return new RequestBody() {
            @Override
            public long contentLength() throws IOException {
                return length;
            }

            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                byte[] buf = new byte[128];
                while (inputStream.read(buf) != -1) {
                    sink.write(buf);
                }
            }
        };
    }

    static boolean isEmpty(Object obj) {
        if (obj == null) return true;
        if (obj instanceof String) {
            return ((String) obj).isEmpty();
        } else if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        } else if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        }
        return false;
    }
}
