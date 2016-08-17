package com.zedcn.qingstor.api;

import com.zedcn.qingstor.elements.QingStorBucket;
import com.zedcn.qingstor.elements.QingStorObject;

/**
 * 青云对象Api
 *
 * <a href="https://docs.qingcloud.com/qingstor/api/object/index.html">Goto Doc's Site</a>
 * Created by Zed on 2016/8/16.
 */
public interface ObjectApi {

    /**
     * 获取一个青云存储对象
     *
     * @param objectName 对象名称
     * @return 青云存储对象
     */
    QingStorObject get(String objectName);

    /**
     * 创建一个青云存储对象
     *
     * @param object 青云存储对象
     */
    void create(QingStorObject object);

    /**
     * 复制一个青云存储对象
     *
     * @param source 复制源
     * @param target 目标
     */
    void copy(String source, String target);

    /**
     * 删除一个青云存储对象
     *
     * @param objectName 对象名称
     */
    void delete(String objectName);

    /**
     * 获取对象是否存在
     *
     * @param objectName 对象名称
     * @return 无Content的Object实例
     */
    QingStorObject exists(String objectName);

    final class Builder{
        public static ObjectApi newApi(QingStorBucket bucket) {
            return new ObjectApiImpl(bucket);
        }
    }
}
