package com.yn.anime.work.controller;

import com.yn.anime.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/*
    classDescription:
*/
@RestController
@RequestMapping("/work/file")
@CrossOrigin("/*")
public class FileController {

    @Value("${file.upload-folder}")
    private String uploadFolder; // 读取配置：${user.dir}/uploads/works/

    @PostMapping("/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.fail("上传文件不能为空"); // 👈 使用你的 ApiResponse.fail
        }
        try {
            // 1. 获取原始文件名并转小写
            String originalFilename = file.getOriginalFilename().toLowerCase();
            // 2. 截取后缀 (例如: .jpg)
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 3. 网页新增数据：生成 UUID 文件名，防止重名覆盖
            String newFileName = UUID.randomUUID().toString() + extension;

            // 4. 写入到本地物理路径
            File dest = new File(uploadFolder + newFileName);
            file.transferTo(dest);

            // 5. 返回纯文件名给前端，包裹进你的 ApiResponse 中
            return ApiResponse.success(newFileName); // 👈 对应数据装入 data 返回 200
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.fail("文件上传服务器失败");
        }
    }
}