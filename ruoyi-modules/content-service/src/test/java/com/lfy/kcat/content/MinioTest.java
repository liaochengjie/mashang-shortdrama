package com.lfy.kcat.content;

import io.minio.*;
import io.minio.http.Method;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.github.therapi.runtimejavadoc.repack.com.eclipsesource.json.Json.object;

public class MinioTest {


    @Test
    public void minioTest() throws Exception {
        String bucketName="kcat";
        String filePath="C:\\Users\\廖成杰\\Desktop\\萧炎.jpg";
        FileInputStream fileInputStream = new FileInputStream(filePath);

        //1.创建一个MinioClient
        MinioClient client = MinioClient.builder()
            .endpoint("http://localhost:9000")
            .credentials("ruoyi", "ruoyi123")
            .build();
        System.out.println(client);

        //2.判断桶是否存在，如果不存在那么创建一个桶
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
            .bucket(bucketName)
            .build();
        boolean exists = client.bucketExists(bucketExistsArgs);
        if(!exists){
            System.out.println("该桶不存在，现在开始创建一个名为"+bucketName+"的桶");
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                .bucket(bucketName)
                .build();
            client.makeBucket(makeBucketArgs);
        }else{
            System.out.println(bucketName+"桶已经存在");
        }

        String fileName=UUID.randomUUID().toString()+"_萧炎.jpg";

        //3.上传文件
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
            .bucket(bucketName)
            .stream(fileInputStream, fileInputStream.available(), -1)
            .object(fileName)
            .contentType("image/jpeg")
            .build();
        client.putObject(putObjectArgs);


        //私有桶
        GetPresignedObjectUrlArgs build = GetPresignedObjectUrlArgs.builder()
            .method(Method.GET)
            .bucket(bucketName)
            .object(fileName)
            .expiry(1, TimeUnit.DAYS)
            .build();
        System.out.println(client.getPresignedObjectUrl(build));
    }
}
