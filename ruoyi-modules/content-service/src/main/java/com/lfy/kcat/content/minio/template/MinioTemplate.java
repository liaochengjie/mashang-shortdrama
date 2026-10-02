package com.lfy.kcat.content.minio.template;

import com.lfy.kcat.content.biz.RagReleaseService;

import com.lfy.kcat.content.minio.properties.MyMinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.enums.FormatsType;
import org.dromara.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


import java.util.Date;
import java.util.UUID;


/**
 * @author liaochengjie
 */
@Component
@Slf4j
public class MinioTemplate {
    @Autowired
    private RagReleaseService ragReleaseService;
    String BUCKET_NAME="kcat";
    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MyMinioProperties myMinioProperties;
    public String uploadWebFile(MultipartFile file) {
        String url="";
        try {
            //获取文件名
            String fileName = file.getOriginalFilename();
            //添加时间为名字前缀来导致可以通过时间排序
            String timePrefix = DateUtils.parseDateToStr(FormatsType.YYYY_MM_DD_SLASH,new Date());
            //添加UUID防止重复导致覆盖
            //文件名称的组成：路径+UUID+文件名+文件类型
            String objectName= timePrefix+"/"+UUID.randomUUID().toString()+"_"+fileName;
            //获取文件类型
            String contentType = file.getContentType();
            //获取文件大小
            long size = file.getSize();

            //判断桶是否存在
            //这步过后，桶一定存在了
            bucketExistAndCreate(BUCKET_NAME);


            java.security.MessageDigest checksum = java.security.MessageDigest.getInstance("SHA-256");
            try (java.security.DigestInputStream stream = new java.security.DigestInputStream(file.getInputStream(), checksum)) {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .contentType(contentType)
                .stream(stream, size, -1)
                .object(objectName)
                .build();
            minioClient.putObject(putObjectArgs);
            }
            url= myMinioProperties.getEndpoint()+"/"+BUCKET_NAME+"/"+objectName;
            ragReleaseService.registerAsset(url, objectName, java.util.HexFormat.of().formatHex(checksum.digest()), size);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return url;
    }

    private void bucketExistAndCreate(String bucketName) {
        //判断桶是否存在
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
            .bucket(BUCKET_NAME)
            .build();
        boolean bucketExists;
        try {
            bucketExists=minioClient.bucketExists(bucketExistsArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (!bucketExists) {
            log.info("bucket{}未存在，现在进行创建",bucketName);
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                .bucket(BUCKET_NAME)
                .build();
            try {
                minioClient.makeBucket(makeBucketArgs);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            log.info("bucket{}存在",bucketName);
        }
    }
}
