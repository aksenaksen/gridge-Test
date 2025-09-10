package com.example.instagram.comment.infrastructor;

import com.example.instagram.comment.domain.Comment;
import com.example.instagram.comment.domain.CommentPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    Long countCommentsByFeedId(Long feedId);
}