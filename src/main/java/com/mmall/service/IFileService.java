package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by user on 2018/3/11.
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
