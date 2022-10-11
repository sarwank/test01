package com.cdms;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

// Minio Configuration file
@Configuration
public class MinioConfiguration {

    // Retrieve endpoint
    @Value("${minio.endpoint}")
    private String minioEndpoint;

    // Retrieve Access Key
    @Value("${minio.credentials.access-key}")
    private String minioAccessKey;

    // Retrieve Secret Key
    @Value("${minio.credentials.secret-key}")
    private String minioSecretKey;

    @Primary
    @Bean
    public MinioClient getMinioBuilder() {
        return MinioClient.builder()
                .endpoint(minioEndpoint)
                .credentials(minioAccessKey, minioSecretKey)
                .build();
    }

//    @Primary
//    @Bean
//    public MinioClient getMinioBuilder() {
//        return MinioClient.builder()
//                .endpoint("http://192.168.1.118:9000")
//                .credentials("A9OQV8nF4xuVvcdF", "ShOg6CMvnH4JhrH3srwBczpuMEK9yVeN")
////                .credentials("ogUxOV6OFM9KAfPr","yqsVKrDKZmVmEw1VuYlMXxrskQOiOuBM")
//                .build();
//    }
}
