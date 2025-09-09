package com.example.instagram.comment.domain;

public record CommentPage(
        Long pageSize,
        String lastPath
) {
}
