package com.example.instagram.feed_read.client;

import com.example.instagram.comment.application.CommentService;
import com.example.instagram.feed_read.application.CommentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentServiceClient implements CommentClient {

    private final CommentService commentService;

    @Override
    public Long getCount(Long feedId) {
        return commentService.count(feedId);
    }
}
