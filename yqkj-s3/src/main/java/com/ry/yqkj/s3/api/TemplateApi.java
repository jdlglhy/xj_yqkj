package com.ry.yqkj.s3.api;

import com.amazonaws.services.s3.model.Bucket;
import com.ry.yqkj.s3.model.AttachmentVO;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public interface TemplateApi {
    /**
     * 获取所有的bucket
     */
    List<Bucket> getAllBuckets();


    /**
     * 创建bucket
     *
     * @param bucketName bucket 名称
     */
    void createBucket(String bucketName);


    /**
     * 移除bucket
     *
     * @param bucketName bucket 名称
     */
    void removeBucket(String bucketName);

    /**
     * 删除objectName
     *
     * @param bucketName bucket
     * @param objectName 需要拼接 objectName
     */
    void deleteObjectName(String bucketName, String objectName);

    /**
     * @param bucketName  如 sucheon-scpc
     * @param folder      可为空
     * @param objectName  如 test.png,/f1/zz/xxx.txt
     * @param inputStream 文件流
     * @return 上传结果
     */
    AttachmentVO upload(String bucketName, String folder, String objectName, InputStream inputStream);


    /**
     * @param bucketName
     * @param folder      可为空
     * @param objectName
     * @param contentType
     * @param inputStream
     * @return
     */
    AttachmentVO upload(String bucketName, String folder, String objectName, String contentType, InputStream inputStream);


    /**
     * 获取objectUrl
     *
     * @param bucketName bucket 名称
     * @param objectName 如 test.png,/f1/zz/xxx.txt
     * @param expires    时间
     */
    URL getObjectURL(String bucketName, String objectName, Integer expires);

}
