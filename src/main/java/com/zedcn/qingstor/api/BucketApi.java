package com.zedcn.qingstor.api;

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
     * 列出所有Bucket
     *
     * @param perfix 限定前缀
     * @return BucketList
     */
    List<QingStorBucket> listBucket(String perfix);

    /**
     * 列出所有Bucket
     *
     * @param perfix 限定前缀
     * @param limit  限定集合最大数量
     * @return BucketList
     */
    List<QingStorBucket> listBucket(String perfix, int limit);

    /**
     * 创建一个Bucket
     *
     * @param bucketName Bucket名称
     * @return 创建结果
     */
    boolean createBucket(String bucketName);

    /**
     * 删除一个Bucket
     * @param bucketName Bucket名称
     * @return 删除结果
     */
    boolean deleteBucket(String bucketName);

    /**
     * 判断一个Bucket是否存在和是否有权读取
     * @param bucketName Bucket名称
     * @return 可访问返回200，否则返回403/404。
     */
    int exists(String bucketName);



}
