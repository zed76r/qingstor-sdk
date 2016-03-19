package com.zedcn.qingstor.elements;

import java.util.Date;

/**
 * 青云对象存储访问基本信息
 * Created by Zed on 2016/3/19.
 */
@SuppressWarnings("unused")
public class QingStorBucket extends QingCloudAccess {
    /**
     * 对象存储Bucket实例名称
     */
    private String name;
    /**
     * Bucket数量
     */
    private int count;
    /**
     * Bucket总大小
     */
    private long size;
    /**
     *Buecket创建时间
     */
    private Date created;
    /**
     * 对象状态
     */
    private String status;

    public QingStorBucket() {
        super();
    }

    public String getName() {
        return name;
    }

    public QingStorBucket setName(String name) {
        this.name = name;
        return this;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
