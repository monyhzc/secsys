package com.itmk.web.upload.controller;

import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${web.upload-path}")
    private String uploadPath;

    @PostMapping
    public ResultVo upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResultUtils.error("上传文件不能为空");
        }

        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件后缀
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // 生成新文件名
        String newFileName = UUID.randomUUID().toString() + suffixName;

        File dest = new File(uploadPath + newFileName);
        // 检测目录是否存在
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        try {
            file.transferTo(dest);
            // 返回访问路径，假设前端通过 /images/文件名 访问
            // 注意：这里返回的路径需要配合 WebMvcConfig 的资源映射
            String fileUrl = "/images/" + newFileName;
            return ResultUtils.success("上传成功", fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtils.error("上传失败");
        }
    }
}
