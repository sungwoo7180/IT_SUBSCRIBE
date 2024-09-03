package com.sw.journal.journalcrawlerpublisher.repository;

import com.sw.journal.journalcrawlerpublisher.domain.Reply;
import com.sw.journal.journalcrawlerpublisher.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    // 대댓글 리스트를 부모 댓글 기준으로 최신순으로 가져오는 쿼리
    List<Reply> findByParentCommentOrderByCreatedAtDesc(Comment parentComment);

    // 특정 부모 댓글에 달린 대댓글의 수를 계산하는 쿼리
    @Query("SELECT COUNT(r) FROM Reply r WHERE r.parentComment = :parentComment")
    int countByParentComment(@Param("parentComment") Comment parentComment);

}
