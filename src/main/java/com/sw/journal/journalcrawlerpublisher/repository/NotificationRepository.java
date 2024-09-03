package com.sw.journal.journalcrawlerpublisher.repository;

import com.sw.journal.journalcrawlerpublisher.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 특정 사용자의 읽지 않은 알림을 찾는 메서드
    List<Notification> findByMemberIdAndIsReadFalse(Long memberId);

    // 특정 사용자의 모든 알림을 찾는 메서드 추가
    List<Notification> findByMemberId(Long memberId);

}