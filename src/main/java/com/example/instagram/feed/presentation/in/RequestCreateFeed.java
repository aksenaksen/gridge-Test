package com.example.instagram.feed.presentation.in;

import com.example.instagram.feed.application.dto.in.FeedCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

import static com.example.instagram.feed.constant.FeedMessageConstant.EMPTY_FEED;
import static com.example.instagram.feed.constant.FeedMessageConstant.NOT_MATCHED_CONTENT;

public record RequestCreateFeed(
        @NotEmpty(message = EMPTY_FEED)
        @NotNull(message = EMPTY_FEED)
        @Size(max = 200, message = NOT_MATCHED_CONTENT)
        String content
) {
    public FeedCreateCommand toCommand(Long writerId, List<String> imageUrls) {
        return new FeedCreateCommand(writerId, content, imageUrls);
    }
}