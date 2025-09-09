package com.example.instagram.comment.application;

import com.example.instagram.comment.domain.Comment;
import com.example.instagram.comment.exception.CommentNotFoundException;
import com.example.instagram.comment.infrastructor.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentFinder {

    private final CommentRepository commentRepository;

    public List<Comment> findAll(Long feedId, String lastPath, Long pageSize) {

        return lastPath == null ?
                commentRepository.findAllByFeedId(feedId, pageSize) :
                commentRepository.findAllByFeedId(feedId, lastPath, pageSize);
    }

    public Comment findById(Long commentId){
        return commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
    }

    public Long countByFeedId(Long feedId) {
        return commentRepository.countCommentsByFeedId(feedId);
    }
}
