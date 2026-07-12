package com.yn.anime.character.controller;

import com.yn.anime.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/*
    classDescription:
*/
@RestController
@RequestMapping("/character/file")
public class FileController {

    @Value("${file.upload-folder}")
    private String uploadFolder; // 读取配置：${user.dir}/uploads/characters/

    @PostMapping("/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.fail("上传文件不能为空");
        }
        try {
            // 1. 获取原始文件名并转小写
            String originalFilename = file.getOriginalFilename().toLowerCase();
            // 2. 提取后缀 (例如: .png)
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 3. 生成 UUID 文件名，防止重名覆盖
            String newFileName = UUID.randomUUID().toString() + extension;

            // 4. 写入到本微服务的子物理路径
            File dest = new File(uploadFolder + newFileName);
            file.transferTo(dest);

            // 5. 返回纯文件名给前端
            return ApiResponse.success(newFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.fail("角色头像上传服务器失败");
        }
    }
}