package com.asusoftware.socialapp.notification.service;

import com.asusoftware.socialapp.notification.model.Notification;
import com.asusoftware.socialapp.notification.model.NotificationType;
import com.asusoftware.socialapp.notification.model.dto.NotificationDTO;
import com.asusoftware.socialapp.notification.repository.NotificationRepository;
import com.asusoftware.socialapp.post.model.Post;
import com.asusoftware.socialapp.post.repository.PostRepository;
import com.asusoftware.socialapp.user.model.User;
import com.asusoftware.socialapp.user.repository.UserRepository;
import lombok.Data;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@Data
public class NotificationService {


    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private final SimpMessagingTemplate messagingTemplate;


    public Notification createNotification(UUID initiatorUserId, UUID recipientUserId, UUID postId, NotificationType type) {
        User initiator = userRepository.findById(initiatorUserId).orElseThrow(() -> new RuntimeException("User not found"));
        User recipient = userRepository.findById(recipientUserId).orElseThrow(() -> new RuntimeException("User not found"));

        Post post = null;
        if (postId != null && !type.equals(NotificationType.FOLLOW)) {
            post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        }

        String message = constructNotificationMessage(initiator, type);

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setType(type);
        notification.setRecipient(recipient); // Recipient of the notification
        notification.setInitiator(initiator); // Set the initiator of the notification
        if (post != null) notification.setPost(post); // For LIKE and COMMENT, not for FOLLOW
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        Notification savedNotification = notificationRepository.save(notification);

        // Emit the notification
        emitNotification(savedNotification, recipientUserId, initiator.getProfileImage());

        return savedNotification;
    }

    private String constructNotificationMessage(User initiator, NotificationType type) {
        String userAction = switch (type) {
            case LIKE -> "liked";
            case COMMENT -> "commented on";
            case FOLLOW -> "started following";
        };

        return switch (type) {
            case FOLLOW -> String.format("%s %s you.", initiator.getFirstName(), userAction);
            default -> String.format("%s %s your post.", initiator.getFirstName(), userAction);
        };
    }

    private void emitNotification(Notification notification, UUID recipientUserId, String profileImageUrl) {
        // Assuming you have a DTO to include both the Notification and profile image URL
        NotificationDTO dto = NotificationDTO.toDto(notification, profileImageUrl);
        messagingTemplate.convertAndSendToUser(recipientUserId.toString(), "/queue/notifications", dto);
    }


}
