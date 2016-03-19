package com.zedcn.qingstor.excption;

/**
 * 签名构造异常
 * Created by Zed on 2016/3/19.
 */
public class SignExption extends RuntimeException {
    public SignExption() {
        super("签名时遇到问题");
    }
}
