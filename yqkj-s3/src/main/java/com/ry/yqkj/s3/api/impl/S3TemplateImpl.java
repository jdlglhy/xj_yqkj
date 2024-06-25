package com.ry.yqkj.s3.api.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.util.StringUtils;
import com.ry.yqkj.s3.api.TemplateApi;
import com.ry.yqkj.s3.config.ConfigProperties;
import com.ry.yqkj.s3.model.AttachmentVO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Slf4j
public class S3TemplateImpl implements TemplateApi {
    protected final AmazonS3 amazonS3;

    protected final ConfigProperties properties;

    public S3TemplateImpl(AmazonS3 amazonS3, ConfigProperties properties) {

        this.amazonS3 = amazonS3;
        this.properties = properties;
    }

    @Override
    public List<Bucket> getAllBuckets() {
        List<Bucket> buckets = amazonS3.listBuckets();
        log.info("buckets={}", buckets);
        return null;
    }

    @Override
    public void createBucket(String bucketName) {

        if (amazonS3.doesBucketExistV2(bucketName)) {
            log.warn("createBucket bucketName={} is existed.", bucketName);
            return;
        }
        amazonS3.createBucket(bucketName);
    }

    @Override
    public void removeBucket(String bucketName) {

        if (amazonS3.doesBucketExistV2(bucketName)) {
            log.warn("removeBucket bucketName={} is not existed.", bucketName);
            return;
        }
        amazonS3.deleteBucket(bucketName);
    }


    @Override
    public void deleteObjectName(String bucketName, String objectName) {
        amazonS3.deleteObject(bucketName, objectName);
    }


    private String getDefaultBucket(String bucketName) {
        if (StringUtils.isNullOrEmpty(bucketName)) {
            bucketName = properties.getBucketName();
        }
        return bucketName;
    }

    @Override
    @SneakyThrows
    public AttachmentVO upload(String bucketName, String folder, String objectName, InputStream inputStream) {

        return upload(getDefaultBucket(bucketName), folder, objectName, "application/octet-stream", inputStream);
    }


    @Override
    @SneakyThrows
    public AttachmentVO upload(String bucketName, String folder, String fileName, String contentType, InputStream inputStream) {
        bucketName = getDefaultBucket(bucketName);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(bytes.length);
        objectMetadata.setContentType(contentType);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        String dataTimeFmt = DateUtil.format(new Date(), "yyyyMMddHHmmss");
        String filePath = path(folder) + dataTimeFmt + "_" + fileName;
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            amazonS3.createBucket(bucketName);
        }
        PutObjectResult result = amazonS3.putObject(bucketName, filePath, byteArrayInputStream, objectMetadata);
        log.info("响应：{}", JSON.toJSONString(result));
        return render(bucketName, fileName, filePath);
    }


    private String path(String folder) {
        if (StrUtil.isBlank(folder)) {
            return folder;
        }
        if (!StrUtil.endWith(folder, "/")) {
            folder = folder + "/";
        }
        return folder;
    }

    @Override
    @SneakyThrows
    public URL getObjectURL(String bucketName, String objectName, Integer expires) {

        bucketName = getDefaultBucket(bucketName);
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, expires);
        return amazonS3.generatePresignedUrl(bucketName, objectName, calendar.getTime());
    }

    private AttachmentVO render(String bucketName, String fileName, String filePath) {
        String fileType = StrUtil.subAfter(fileName, ".", true);
        return AttachmentVO.builder().fileOriName(fileName).filePath(bucketName + "/" + filePath).fileType(fileType).build();
    }


}
