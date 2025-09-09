package com.example.instagram.feed.application.feed;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileUploadService {
    
    String uploadSingleFile(MultipartFile file, String directory);
    
    List<String> uploadMultipleFiles(List<MultipartFile> files, String directory);
    
    void deleteFile(String fileUrl);

}