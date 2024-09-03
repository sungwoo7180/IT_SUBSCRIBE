package com.sw.journal.journalcrawlerpublisher.service;
import com.sw.journal.journalcrawlerpublisher.domain.Member;
import com.sw.journal.journalcrawlerpublisher.domain.Notification;
import com.sw.journal.journalcrawlerpublisher.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class NotificationService {

    // 사용자 ID 별로 SSE Emitter 를 관리
    private final ConcurrentMap<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final NotificationRepository notificationRepository; // Repository 주입

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // SSE 연결을 생성하고 관리
    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter();
        emitters.put(userId, emitter);

        // 연결이 완료되거나 타임아웃되면 emitter 를 제거
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        return emitter;
    }

    // 알림 전송
    public void sendNotification(Long userId, String message) {
        // 알림을 DB에 저장 (모든 알림 저장)
        saveNotification(userId, message);

        // 사용자가 현재 로그인 상태라면 실시간 알림 전송
        SseEmitter emitter = emitters.get(userId);

//        if (emitter != null) {
//            try {
//                // JSON 객체 생성 및 메시지 추가
//                Map<String, String> messageMap = new HashMap<>();
//                messageMap.put("message", message);
//
//                ObjectMapper objectMapper = new ObjectMapper();
//                String jsonMessage = objectMapper.writeValueAsString(messageMap);
//
//                // 실시간 알림을 JSON 형식으로 전송
//                emitter.send(SseEmitter.event().name("message").data(jsonMessage));
//            } catch (IOException e) {
//                emitter.completeWithError(e);
//                emitters.remove(userId);
//            }
//        }
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("message").data(message)); // 단순 문자열 전송
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(userId);
            }
        }
    }

    // 알림을 DB에 저장
    private void saveNotification(Long userId, String message) {
        Notification notification = Notification.builder()
                .member(new Member(userId))  // Member 객체 설정
                .message(message)
                .createdAt(LocalDateTime    .now())
                .isRead(false) // 기본값으로 읽지 않음 상태로 설정
                .build();

        notificationRepository.save(notification); // 알림 DB에 저장
    }

    // 사용자가 로그인할 때 저장된 알림을 가져오는 메서드
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByMemberIdAndIsReadFalse(userId);
    }

    // 알림 읽음 처리
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid notification ID"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // "모두 읽기" 처리
    public void markAllNotificationsAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByMemberIdAndIsReadFalse(userId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }
    // 모든 알림 조회 메서드 추가
    public List<Notification> getAllNotifications(Long userId) {
        return notificationRepository.findByMemberId(userId);
    }
}
