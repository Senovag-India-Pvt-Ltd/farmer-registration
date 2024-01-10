package com.sericulture.registration.controller;//package com.sericulture.registration.controller;//package com.sericulture.registration.controller;

import com.sericulture.registration.service.S3Service;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @GetMapping("/list")
    public ResponseEntity<?> listFiles(

    ) {
        val body = s3Service.listFiles();
        return ResponseEntity.ok(body);
    }

    @PostMapping("/upload")
    public void uploadFile(@RequestParam("multipartFile") MultipartFile file,
                           @RequestParam("fileName") String fileName) throws Exception {
        s3Service.uploadFile(fileName, file.getSize(), file.getContentType(), file.getInputStream());
    }

    @SneakyThrows
    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(
            @RequestParam("fileName") String fileName) throws Exception {
        val body = s3Service.downloadFile(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; ")
                .body(body.toByteArray());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(
            @RequestParam("fileName") String fileName
    ) {
        s3Service.deleteFile(fileName);
        return ResponseEntity.ok().build();
    }
}
