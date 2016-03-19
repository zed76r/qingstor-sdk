package com.zedcn.qingstor.excption;

/**
 * 对象Put操作异常
 * Created by Zed on 2016/3/19.
 */
public class ObjectPutExcption extends RequestExcption {
    public ObjectPutExcption(int code) {
        super("Object put response is " + code);
    }
}
