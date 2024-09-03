package com.sw.journal.journalcrawlerpublisher.controller;
import com.sw.journal.journalcrawlerpublisher.domain.Notification;
import com.sw.journal.journalcrawlerpublisher.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // SSE 연결을 설정하는 엔드포인트
    @GetMapping("/stream/{userId}")
    public SseEmitter streamNotifications(@PathVariable Long userId) {
        // 사용자가 실시간 알림을 수신하기 위해 SSE 연결을 설정
        SseEmitter emitter = notificationService.createEmitter(userId);

        // 사용자가 로그인 시 저장된 알림을 전송
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        notifications.forEach(notification -> {
            try {
                emitter.send(SseEmitter.event().name("message").data(notification.getMessage()));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    // 개별 알림 읽기 처리
    @PostMapping("/read/{notificationId}")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    // 모든 알림 읽기 처리
    @PostMapping("/read-all/{userId}")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable Long userId) {
        notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok().build();
    }

    // 모든 알림 조회 (읽은 알림 포함)
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Notification>> getAllNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getAllNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // 임의로 알림을 전송하는 엔드포인트
    @PostMapping("/send/{userId}")
    public void sendNotification(@PathVariable Long userId, String message) {
        notificationService.sendNotification(userId, message);  // 메서드 호출
    }
}
