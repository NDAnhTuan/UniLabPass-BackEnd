package com.example.UniLabPass.controller;
import com.example.UniLabPass.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final CloudinaryService cloudinaryService;

    public FileUploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/upload/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                         @RequestParam("folder") String folderName) throws IOException {
        return ResponseEntity.ok(cloudinaryService.uploadFile(file, folderName));
    }

    @PostMapping("/upload/video")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file,
                                         @RequestParam("folder") String folderName) throws IOException {
        return ResponseEntity.ok(cloudinaryService.uploadVideo(file, folderName));
    }
}