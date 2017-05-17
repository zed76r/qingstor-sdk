package com.zedcn.qingstor.excption;

import java.io.IOException;

/**
 * 请求API异常
 * Created by Zed on 2016/3/19.
 */
public class RequestExcption extends RuntimeException {
    @SuppressWarnings("WeakerAccess")
    public RequestExcption() {
        super();
    }

    @SuppressWarnings("WeakerAccess")
    public RequestExcption(String msg) {
        super(msg);
    }

    public RequestExcption(IOException e) {
        super(e);
    }
}
