package com.zedcn.qingstor.api;

import com.zedcn.qingstor.elements.QingCloudAccess;
import com.zedcn.qingstor.elements.QingStorBucket;

import java.util.List;

/**
 * 青云BucketAPI
 *
 * <a href="https://docs.qingcloud.com/qingstor/api/bucket/index.html">Goto Doc's Site</a>
 * Created by Zed on 2016/8/16.
 */
public interface BucketApi {

    /**
     * 列出所有Bucket
     *
     * @return BucketList
     */
    List<QingStorBucket> listBucket();

    /**
     * 创建一个Bucket
     *
     * @param bucketName Bucket名称
     */
    void createBucket(String bucketName);

    /**
     * 删除一个Bucket
     * @param bucketName Bucket名称
     */
    void deleteBucket(String bucketName);

    /**
     * 判断一个Bucket是否存在和是否有权读取
     * @param bucketName Bucket名称
     * @return 可访问返回200，否则返回403/404。
     */
    boolean exists(String bucketName);


    final class Builder{
        public static BucketApi newApi(QingCloudAccess access){
            return new BucketApiImpl(access);
        }
    }

}
