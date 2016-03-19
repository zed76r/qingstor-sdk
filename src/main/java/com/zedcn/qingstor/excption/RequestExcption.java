package com.zedcn.qingstor.excption;

/**
 * 请求API异常
 * Created by Zed on 2016/3/19.
 */
public class RequestExcption extends RuntimeException {
    public RequestExcption() {
    }

    public RequestExcption(String msg) {
        super(msg);
    }
}
