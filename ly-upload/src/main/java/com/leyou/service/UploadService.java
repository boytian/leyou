package com.leyou.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @Author: TianTian
 * @Date: 2020/4/23 18:27
 */
public interface UploadService {
    String upload(MultipartFile file);

    Map<String,Object> getSignature();
}
