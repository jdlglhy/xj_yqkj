package com.ry.yqkj.framework.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.ry.yqkj.framework.config.s3.S3ConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;


@Configuration
public class S3Configuration {

    @Resource
    private S3ConfigProperties s3ConfigProperties;

    @Bean
    public AmazonS3 ossClient() {
        // 客户端配置，主要是全局的配置信息
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setMaxConnections(s3ConfigProperties.getMaxConnections());
        // url以及region配置
        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
                s3ConfigProperties.getEndpoint(), s3ConfigProperties.getBucketName());
        // 凭证配置
        AWSCredentials awsCredentials = new BasicAWSCredentials(s3ConfigProperties.getAccessKey(),
                s3ConfigProperties.getSecretKey());
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        // build amazonS3Client客户端
        return AmazonS3Client.builder().withEndpointConfiguration(endpointConfiguration)
                .withClientConfiguration(clientConfiguration).withCredentials(awsCredentialsProvider)
                .disableChunkedEncoding().withPathStyleAccessEnabled(s3ConfigProperties.getPathStyleAccess()).build();
    }

//    @Bean
//    @ConditionalOnBean(AmazonS3.class)
//    public TemplateApi templateApi(AmazonS3 amazonS3, ConfigProperties configProperties){
//        return new S3TemplateImpl(amazonS3,configProperties);
//    }

}
