package com.zedcn.qingstor.api;

/**
 * 中断器
 * Created by Zed on 2016/8/16.
 */
public final class Assert {
    public static void notNull(Object o){
        if(o == null)
            throw  new IllegalArgumentException("must be not null!");
    }
    public static void notEmpty(String v) {
        if (v == null || v.isEmpty())
            throw new IllegalArgumentException("must be not empty!");
    }
}
