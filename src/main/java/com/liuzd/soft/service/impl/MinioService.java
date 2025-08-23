package com.liuzd.soft.service.impl;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * @author: liuzd
 * @date: 2025/8/22
 * @email: liuzd2025@qq.com
 * @desc
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    /**
     * 检查存储桶是否存在
     *
     * @return
     */
    public boolean bucketExists() {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("检查存储桶是否存在失败: ", e);
            return false;
        }
    }

    /**
     * 创建存储桶
     */
    public void createBucket() {
        try {
            if (!bucketExists()) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            log.error("创建存储桶失败: ", e);
        }
    }

    /**
     * 上传文件
     *
     * @param file       文件
     * @param objectName 对象名称
     * @return
     */
    public boolean uploadFile(MultipartFile file, String objectName) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return true;
        } catch (Exception e) {
            log.error("上传文件失败: ", e);
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param objectName 对象名称
     */
    public boolean deleteFile(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return true;
        } catch (Exception e) {
            log.error("删除文件失败: ", e);
            return false;
        }
    }

    /**
     * 获取文件访问URL
     *
     * @param objectName 对象名称
     * @return
     */
    public String getFileUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(7, TimeUnit.DAYS)
                            .build());
        } catch (Exception e) {
            log.error("获取文件访问URL失败: ", e);
            return null;
        }
    }

    /**
     * 下载文件
     *
     * @param objectName 对象名称
     * @return
     */
    public InputStream downloadFile(String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            log.error("下载文件失败: ", e);
            return null;
        }
    }
}
