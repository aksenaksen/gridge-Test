package com.example.instagram.comment.infrastructor;

import com.example.instagram.comment.domain.Comment;
import com.example.instagram.comment.domain.QComment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public Optional<Comment> findByPath(String path) {
        QComment comment = QComment.comment;
        
        Comment result = queryFactory
                .selectFrom(comment)
                .where(comment.commentPath.path.eq(path))
                .fetchOne();
        
        return Optional.ofNullable(result);
    }
    
    @Override
    public Optional<String> findDescendantsTopPath(Long feedId, String pathPrefix) {
        QComment comment = QComment.comment;
        
        String result = queryFactory
                .select(comment.commentPath.path)
                .from(comment)
                .where(
                    comment.feedId.eq(feedId)
                            .and(comment.commentPath.path.gt(pathPrefix))
                            .and(comment.commentPath.path.like(pathPrefix + "%"))
                )
                .orderBy(comment.commentPath.path.desc())
                .limit(1)
                .fetchOne();
        
        return Optional.ofNullable(result);
    }

    @Override
    public List<Comment> findChildsByPath(Long feedId, String pathPrefix){
        QComment comment = QComment.comment;

        return queryFactory
                .selectFrom(comment)
                .where(
                        comment.feedId.eq(feedId)
                                .and(comment.commentPath.path.gt(pathPrefix))
                                .and(comment.commentPath.path.like(pathPrefix + "%"))
                )
                .fetch();
    }

    @Override
    public List<Comment> findAllByFeedId(Long feedId, Long limit) {
        QComment comment = QComment.comment;
        
        return queryFactory
                .selectFrom(comment)
                .where(comment.feedId.eq(feedId))
                .orderBy(comment.commentPath.path.asc())
                .limit(limit)
                .fetch();
    }
    
    @Override
    public List<Comment> findAllByFeedId(Long feedId, String lastPath, Long limit) {
        QComment comment = QComment.comment;
        
        return queryFactory
                .selectFrom(comment)
                .where(
                    comment.feedId.eq(feedId)
                    .and(comment.commentPath.path.gt(lastPath))
                )
                .orderBy(comment.commentPath.path.asc())
                .limit(limit)
                .fetch();
    }
}