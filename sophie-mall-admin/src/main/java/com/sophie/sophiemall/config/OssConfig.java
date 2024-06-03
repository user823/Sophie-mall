package com.sophie.sophiemall.config;

import com.aliyun.oss.OSSClient;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.minio.MinioClient;

/**
 * Oss对象存储相关配置
 */
@Configuration
public class OssConfig {
    @Value("${aliyun.oss.endpoint}")
    private String ALIYUN_OSS_ENDPOINT;
    @Value("${aliyun.oss.accessKeyId}")
    private String ALIYUN_OSS_ACCESSKEYID;
    @Value("${aliyun.oss.accessKeySecret}")
    private String ALIYUN_OSS_ACCESSKEYSECRET;

    @Value("${minio.endpoint}")
    private String MINIO_OSS_ENDPOINT;
    @Value("${minio.accessKey}")
    private String MINIO_OSS_ACCESSKEY;
    @Value("${minio.secretKey}")
    private String MINIO_OSS_SECRETKEY;

    @Bean
    public OSSClient ossClient(){
        return new OSSClient(ALIYUN_OSS_ENDPOINT,ALIYUN_OSS_ACCESSKEYID,ALIYUN_OSS_ACCESSKEYSECRET);
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(MINIO_OSS_ENDPOINT)
                .credentials(MINIO_OSS_ACCESSKEY, MINIO_OSS_SECRETKEY)
                .build();
    }
}