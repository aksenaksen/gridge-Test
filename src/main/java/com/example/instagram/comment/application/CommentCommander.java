package com.example.instagram.comment.application;

import com.example.instagram.comment.application.dto.in.CommentBlockCommand;
import com.example.instagram.comment.application.dto.in.CommentCreateCommand;
import com.example.instagram.comment.application.dto.in.CommentDeleteCommand;
import com.example.instagram.comment.application.dto.in.CommentUpdateCommand;
import com.example.instagram.comment.domain.Comment;
import com.example.instagram.comment.domain.CommentPath;
import com.example.instagram.comment.exception.CommentNotFoundException;
import com.example.instagram.comment.exception.CommentParentNotFoundException;
import com.example.instagram.comment.infrastructor.CommentRepository;
import com.example.instagram.common.util.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentCommander {

    private final Snowflake snowflake = new Snowflake();
    private final CommentRepository commentRepository;

    public void createComment(CommentCreateCommand command) {

        Comment parent = findParent(command.parentPath());
        CommentPath parentCommentPath = parent == null ? CommentPath.create("") : parent.getCommentPath();

        commentRepository.save(
                Comment.create(
                        snowflake.nextId(),
                        command.content(),
                        command.feedId(),
                        command.writerId(),
                        parentCommentPath.createChildCommentPath(
                                commentRepository.findDescendantsTopPath(command.feedId(), parentCommentPath.getPath())
                                        .orElse(null)
                        )
                )
        );
    }

    public void updateComment(CommentUpdateCommand command) {

        Comment comment = commentRepository.findById(command.commentId())
                .orElseThrow(CommentNotFoundException::new);

        comment.isOwner(command.userId());
        comment.updateComment(command.content());
    }

    public void delete(CommentDeleteCommand command) {

        Comment comment = commentRepository.findById(command.commentId())
                .orElseThrow(CommentNotFoundException::new);

        comment.isOwner(command.userId());
        comment.delete();

        List<Comment> comments = commentRepository.findChildsByPath(comment.getFeedId(), comment.getCommentPath().getPath());
        comments.forEach(Comment::delete);
    }

    public void block(CommentBlockCommand command) {

        Comment comment = commentRepository.findById(command.commentId())
                .orElseThrow(CommentNotFoundException::new);

        comment.block();

        List<Comment> comments = commentRepository.findChildsByPath(comment.getFeedId(), comment.getCommentPath().getPath());
        comments.forEach(Comment::delete);
    }

    private Comment findParent(String parentPath) {

        if(parentPath == null){
            return null;
        }

        return commentRepository.findByPath(parentPath)
                .filter(comment -> !comment.isDeleted())
                .orElseThrow(CommentParentNotFoundException::new);
    }
}
