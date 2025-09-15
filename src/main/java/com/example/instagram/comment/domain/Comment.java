package com.example.instagram.comment.domain;


import com.example.instagram.comment.exception.AlreadyBlockedOrDeletedException;
import com.example.instagram.comment.exception.CommentNotMatchedUserException;
import com.example.instagram.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@Table(name = "comment")
@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    private Long commentId;
    private String content;
    private Long feedId; // shard key
    private Long writerId;
    @Embedded
    private CommentPath commentPath;
    
    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    public static Comment create(Long commentId, String content, Long articleId, Long writerId, CommentPath commentPath) {
        Comment comment = new Comment();
        comment.commentId = commentId;
        comment.content = content;
        comment.feedId = articleId;
        comment.writerId = writerId;
        comment.commentPath = commentPath;
        comment.status = CommentStatus.ACTIVE;
        return comment;
    }

    public boolean isRoot() {
        return commentPath.isRoot();
    }

    public void updateComment(String content) {
        if(isDeleted() || isBlocked()){
            throw new AlreadyBlockedOrDeletedException();
        }
        this.content = content;
    }

    public void delete() {
        this.status = CommentStatus.DELETED_BY_USER;
    }

    public void block() {
        this.status = CommentStatus.BLOCKED;
    }
    
    public boolean isDeleted() {
        return this.status == CommentStatus.DELETED_BY_USER;
    }

    public boolean isBlocked() {
        return this.status == CommentStatus.BLOCKED;
    }

    public void isOwner(Long writerId){
        if(!Objects.equals(this.writerId, writerId)) throw new CommentNotMatchedUserException();
    }
}