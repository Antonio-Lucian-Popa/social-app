package com.asusoftware.socialapp.notification.controller;

import com.asusoftware.socialapp.notification.model.Notification;
import com.asusoftware.socialapp.notification.model.NotificationType;
import com.asusoftware.socialapp.notification.model.dto.NotificationDTO;
import com.asusoftware.socialapp.notification.service.NotificationService;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Data
@RestController
@RequestMapping(path = "/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // Endpoint for testing purposes
    /*
    @PostMapping("/notifications/send")
    public ResponseEntity<?> sendNotification(@RequestParam UUID senderUserId, @RequestParam UUID recipientUserId, @RequestParam UUID postId, @RequestParam NotificationType notificationType) {
        Notification notification = notificationService.createNotification(senderUserId, recipientUserId,postId, notificationType);
        return ResponseEntity.ok(notification);
    } */

    @GetMapping(path = "/findNotifications/{recipientId}")
    public ResponseEntity<Page<NotificationDTO>> getNotifications(@PathVariable(name = "recipientId") UUID recipientId, Pageable pageable) {
        Page<NotificationDTO> notifications = notificationService.findAllByRecipientId(pageable, recipientId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
