package com.leyou.controller;

import com.leyou.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author: TianTian
 * @Date: 2020/4/23 18:26
 */
@RestController
public class UploadController {
    @Resource
    private UploadService uploadService;//注入service层

    /**
     * 上传图片功能
     * @param file
     * @return url路径
     */
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        // 返回200，并且携带url路径
        return ResponseEntity.ok(this.uploadService.upload(file));
    }

    /**
     * oss
     * @return
     */
    @GetMapping("/signature")
    public ResponseEntity<Map<String,Object>> getSignature() {
        //返回阿里签名

        return ResponseEntity.ok(this.uploadService.getSignature());
    }
}
