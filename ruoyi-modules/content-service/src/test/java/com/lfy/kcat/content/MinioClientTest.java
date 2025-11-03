package com.lfy.kcat.content;


import io.minio.MinioClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest

public class MinioClientTest {
    @Autowired
    private MinioClient minioClient;

    @Test
    public void ClientTest(){

        Assertions.assertNotNull(minioClient);
    }

    @Test
    public void uploadTest1(){

    }
}
