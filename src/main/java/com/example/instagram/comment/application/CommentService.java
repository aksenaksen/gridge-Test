package com.example.instagram.comment.application;

import com.example.instagram.comment.application.dto.in.CommentBlockCommand;
import com.example.instagram.comment.application.dto.in.CommentCreateCommand;
import com.example.instagram.comment.application.dto.in.CommentDeleteCommand;
import com.example.instagram.comment.application.dto.in.CommentUpdateCommand;
import com.example.instagram.comment.application.dto.out.CommentDto;
import com.example.instagram.comment.domain.CommentPage;
import com.example.instagram.common.Page;
import com.example.instagram.feed.application.feed.FeedFinder;
import com.example.instagram.feed.application.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentCommander commentCommander;
    private final CommentFinder commentFinder;
    private final FeedService feedService;

    @Transactional
    public void create(CommentCreateCommand command) {
        feedService.findFeedById(command.feedId());
        commentCommander.createComment(command);
    }

    @Transactional
    public void delete(CommentDeleteCommand command) {
        commentCommander.delete(command);
    }

    @Transactional
    public void update(CommentUpdateCommand command) {
        commentCommander.updateComment(command);
    }

    @Transactional
    public void block(CommentBlockCommand command) {
        commentCommander.block(command);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> findAll(Long feedId, CommentPage page) {
        return commentFinder.findAll(feedId, page.lastPath(), page.pageSize()).stream()
                .map(CommentDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommentDto findById(Long id) {
        return CommentDto.from(commentFinder.findById(id));
    }

    @Transactional(readOnly = true)
    public Long count(Long feedId) {
        return commentFinder.countByFeedId(feedId);
    }
}