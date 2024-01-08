package com.sericulture.registration.controller;//package com.sericulture.registration.controller;//package com.sericulture.registration.controller;

import com.sericulture.registration.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("multipartFile") MultipartFile multipartFile) throws Exception {
        // Convert MultipartFile to File
        File file = new File(multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);

        // Upload to S3
//        s3Service.uploadFile("seri-ap-south-1", "path/in/bucket/filename.ext", file);
        s3Service.uploadFile("seri-ap-south-1", "test/" + file.getName(), file);

        // Cleanup
        file.delete();

        return "File uploaded successfully!";
    }
}
