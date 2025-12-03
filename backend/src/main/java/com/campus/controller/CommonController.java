package com.campus.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.campus.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 通用控制器 (文件上传等)
 */
@Tag(name = "通用接口")
@RestController
@RequestMapping("/api/common")
public class CommonController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${server.port}")
    private String port;

    // 简单的获取本机IP的方法，实际生产建议配置域名
    private String getBaseUrl() {
        return "http://localhost:" + port + "/files/";
    }

    @Operation(summary = "文件上传")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 获取原文件名
        String originalFilename = file.getOriginalFilename();
        // 获取文件后缀
        String suffix = FileUtil.extName(originalFilename);
        // 生成新文件名
        String newFileName = IdUtil.simpleUUID() + "." + suffix;

        // 创建文件对象
        File dest = new File(uploadPath + newFileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        try {
            // 保存文件
            file.transferTo(dest);
            // 返回访问URL
            return Result.success(getBaseUrl() + newFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败");
        }
    }
}
