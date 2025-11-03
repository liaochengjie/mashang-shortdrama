package com.lfy.kcat.content.controller;

import com.lfy.kcat.content.minio.template.MinioTemplate;
import org.dromara.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传
 * 前端访问路由地址为:/content/upload
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@RestController
public class UploadController {
    @Autowired
    MinioTemplate minioTemplate;

    /**
     * 文件上传
     * @param file 文件项
     * @return
     */
    @PostMapping("/upload")
    public R UploadController(@RequestParam("file") MultipartFile file) {

        String fileurl=minioTemplate.uploadWebFile(file);

        return R.ok("success",fileurl);
    }
}
