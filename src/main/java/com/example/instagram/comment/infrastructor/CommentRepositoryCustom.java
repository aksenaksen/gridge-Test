package com.example.instagram.comment.infrastructor;

import com.example.instagram.comment.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryCustom {
    
    Optional<Comment> findByPath(String path);
    
    Optional<String> findDescendantsTopPath(Long articleId, String pathPrefix);

    List<Comment> findChildsByPath(Long feedId, String pathPrefix);

    List<Comment> findAllByFeedId(Long articleId, Long limit);
    
    List<Comment> findAllByFeedId(Long articleId, String lastPath, Long limit);
}