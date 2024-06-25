package com.ry.yqkj.s3.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/*
 * since: 2024/5/15 10:36
 * author: lihy
 * description: s3 配置信息即 oss配置信息
 */
@Data
@ConfigurationProperties(prefix = "s3")
public class ConfigProperties {
    /**
     * 对象存储服务的URL
     */
    private String endpoint;

    /**
     * 区域
     */
    private String bucketName;

    /**
     * true path-style nginx 反向代理和S3默认支持 pathStyle模式 {http://endpoint/bucketname}
     * false supports virtual-hosted-style 阿里云等需要配置为 virtual-hosted-style 模式{http://bucketname.endpoint}
     * 只是url的显示不一样
     */
    private Boolean pathStyleAccess = true;

    /**
     * Access key
     */
    private String accessKey;

    /**
     * Secret key
     */
    private String secretKey;

    /**
     * 最大线程数，默认：100
     */
    private Integer maxConnections = 100;

}
