package com.liuzd.soft.controller;

import com.liuzd.soft.enums.RetEnums;
import com.liuzd.soft.service.impl.MinioService;
import com.liuzd.soft.utils.InputStreamUtils;
import com.liuzd.soft.vo.rets.ResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * MinIO文件管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/minio")
@RequiredArgsConstructor
public class MinioController {

    private final MinioService minioService;

    /**
     * 上传文件
     *
     * @param file       文件
     * @param objectName 对象名称（可选）
     * @return 文件访问URL
     */
    @PostMapping("/upload")
    public ResultMessage uploadFile(@RequestParam(value = "file") MultipartFile file,
                                    @RequestParam(value = "objectName", required = false) String objectName) {
        try {
            // 检查文件是否为空
            if (file == null || file.isEmpty()) {
                return ResultMessage.fail(RetEnums.FAIL.getCode(), "文件不能为空");
            }

            // 如果未指定对象名称，则使用原始文件名
            if (objectName == null || objectName.isEmpty()) {
                objectName = file.getOriginalFilename();
            }

            // 上传文件
            boolean uploaded = minioService.uploadFile(file, objectName);
            if (uploaded) {
                // 获取文件访问URL
                //String fileUrl = minioService.getFileUrl(objectName);
                return ResultMessage.success(objectName);
            } else {
                return ResultMessage.fail(RetEnums.FAIL.getCode(), "文件上传失败");
            }
        } catch (Exception e) {
            log.error("文件上传异常: ", e);
            return ResultMessage.fail(RetEnums.FAIL.getCode(), "文件上传失败");
        }
    }

    /**
     * 下载文件
     *
     * @param objectName 对象名称
     * @return 文件流
     */
    @GetMapping("/download/{objectName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String objectName) {
        try {
            InputStream inputStream = minioService.downloadFile(objectName);
            if (inputStream == null) {
                return ResponseEntity.notFound().build();
            }

            // 读取输入流为字节数组
            ByteArrayOutputStream buffer = InputStreamUtils.readByteArrayOutputStream(inputStream);
            byte[] bytes = buffer.toByteArray();
            inputStream.close();

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String encodedFileName = URLEncoder.encode(objectName, "UTF-8");
            headers.add("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("文件下载异常: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 删除文件
     *
     * @param objectName 对象名称
     * @return 删除结果
     */
    @DeleteMapping("/delete/{objectName}")
    public ResponseEntity<String> deleteFile(@PathVariable String objectName) {
        try {
            boolean deleted = minioService.deleteFile(objectName);
            if (deleted) {
                return ResponseEntity.ok("文件删除成功");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件删除失败");
            }
        } catch (Exception e) {
            log.error("文件删除异常: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件删除异常: " + e.getMessage());
        }
    }

    /**
     * 获取文件访问URL
     *
     * @param objectName 对象名称
     * @return 文件访问URL
     */
    @GetMapping("/url/{objectName}")
    public ResponseEntity<String> getFileUrl(@PathVariable String objectName) {
        try {
            String fileUrl = minioService.getFileUrl(objectName);
            if (fileUrl != null) {
                return ResponseEntity.ok(fileUrl);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取文件URL异常: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
