package com.example.instagram.feed.constant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class FeedMessageConstant {

    public static final List<String> ALLOWED_IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");

    public static final String NOT_ACTIVE_STATUS = "현재 피드가 활성화된 상태가 아닙니다";
    public static final String NOT_MATCHED_WRITER = "요청한 사용자와 작성자가 일치하지않습니다";
    public static final String NOT_FOUND_FEED = "해당 피드를 찾을 수 없습니다";

    public static final String EMPTY_FEED = "피드 내용을 입력해주세요";
    public static final String NOT_MATCHED_CONTENT = "피드 내용은 200자 이하여야 합니다";

}
