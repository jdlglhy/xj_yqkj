package com.ry.yqkj.framework.web.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.util.StringUtils;
import com.ry.yqkj.framework.config.s3.S3ConfigProperties;
import com.ry.yqkj.framework.web.api.TemplateApi;
import com.ry.yqkj.model.common.vo.AttachmentVO;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Slf4j
@Service
public class S3TemplateImpl implements TemplateApi {


    @Resource
    private AmazonS3 amazonS3;

    private S3ConfigProperties properties;

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
    public AttachmentVO upload(String bucketName, String folder,
                               String fileName, String contentType,
                               InputStream inputStream) {
        try{
            bucketName = getDefaultBucket(bucketName);
            byte[] bytes = IOUtils.toByteArray(inputStream);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(bytes.length);
            objectMetadata.setContentType(contentType);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            String dataTimeFmt = DateUtil.format(new Date(), "yyyyMMddHHmmss");

            StringBuilder fileUrl = new StringBuilder();
            fileUrl.append(path(folder)).append(dataTimeFmt);

            @Cleanup
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            BufferedImage bufferedImage = isImage(stream);

            if(bufferedImage != null){
                fileUrl.append("&w=").append(bufferedImage.getWidth()).append("&h=").append(bufferedImage.getHeight());
            }
            fileUrl.append("_").append(fileName);
            String filePath = fileUrl.toString();
            if (!amazonS3.doesBucketExistV2(bucketName)) {
                amazonS3.createBucket(bucketName);
            }
            PutObjectResult result = amazonS3.putObject(bucketName, filePath, byteArrayInputStream, objectMetadata);
            log.info("响应：{}", JSON.toJSONString(result));
            return render(bucketName, fileName, filePath);
        }catch (Exception e){

        }finally {
            if(inputStream != null){
                inputStream.close();
            }
        }
        return null;
    }


    private BufferedImage isImage(InputStream inputStream) {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(inputStream);
            if (null != bi) {
                return bi;
            }
        } catch (Exception e) {
            log.warn("非图片上传..");
        }
        return bi;
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
