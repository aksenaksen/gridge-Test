package com.example.instagram.feed.presentation.in;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.instagram.feed.constant.FeedMessageConstant.ALLOWED_IMAGE_EXTENSIONS;

public record RequestCreateImages(
        List<MultipartFile> images
) {
    public RequestCreateImages {
        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("이미지는 필수 항목입니다.");
        }

        for (MultipartFile image : images) {
            String originalFilename = image.getOriginalFilename();
            if (originalFilename == null) {
                throw new IllegalArgumentException("파일 이름이 유효하지 않습니다.");
            }

            String fileExtension = getFileExtension(originalFilename);
            if (!ALLOWED_IMAGE_EXTENSIONS.contains(fileExtension)) {
                throw new IllegalArgumentException("허용되지 않는 파일 확장자입니다: " + fileExtension);
            }
        }
    }

    private static String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

}
