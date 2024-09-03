package com.sw.journal.journalcrawlerpublisher.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reply")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment parentComment; // 최상위 댓글에 대한 참조

    @ManyToOne
    @JoinColumn(name = "parent_reply_id") // 대댓글에 대한 참조
    private Reply parentReply;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    // Many-to-Many 관계로 좋아요를 누른 사용자들
    @ManyToMany
    @JoinTable(
            name = "reply_likes",
            joinColumns = @JoinColumn(name = "reply_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private Set<Member> likedBy = new HashSet<>();

    public void incrementLikeCount() {
        this.likeCount++;
    }
    public void decrementLikeCount() {
        this.likeCount--;
    }

//    @PrePersist
//    public void incrementReplyCount() {
//        parentComment.setReplyCount(parentComment.getReplyCount() + 1);
//    }
//
//    @PreRemove
//    public void decrementReplyCount() {
//        parentComment.setReplyCount(parentComment.getReplyCount() - 1);
//    }

    // 부모 댓글이 있는지 확인
    public boolean hasParentReply() {
        return this.parentReply != null;
    }
}
