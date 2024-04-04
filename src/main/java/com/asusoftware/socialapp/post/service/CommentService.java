package com.asusoftware.socialapp.post.service;

import com.asusoftware.socialapp.exceptions.UnauthorizedUserException;
import com.asusoftware.socialapp.notification.model.NotificationType;
import com.asusoftware.socialapp.notification.service.NotificationService;
import com.asusoftware.socialapp.post.exception.CommentNotFoundException;
import com.asusoftware.socialapp.post.model.Comment;
import com.asusoftware.socialapp.post.model.Post;
import com.asusoftware.socialapp.post.model.dto.CommentDto;
import com.asusoftware.socialapp.post.model.dto.CreateCommentDto;
import com.asusoftware.socialapp.post.repository.CommentRepository;
import com.asusoftware.socialapp.user.model.User;
import com.asusoftware.socialapp.user.model.dto.UserDto;
import com.asusoftware.socialapp.user.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
   // private final PostRepository postRepository;
    private final PostService postService;
    private final UserService userService;

    private final NotificationService notificationService;

    /*
    /**
     * Create a comment
     * @param postId
     * @param commentDto
     * @return CommentDto
     */ /*
    public CommentDto createComment(UUID postId,UUID userId, CreateCommentDto commentDto) {
        Post post = postService.findById(postId);
        User user = userService.findById(userId);
        Comment comment = commentDto.toEntity(commentDto);
        comment.setUser(user);
        comment.setPost(post);
        return CommentDto.fromEntity(commentRepository.save(comment));
    } */

    @Transactional
    public CommentDto createComment(CreateCommentDto commentDTO) {
        Comment comment = new Comment();
        comment.setValue(commentDTO.getValue());
        comment.setCreatedAt(LocalDateTime.now());

        // Fetch post and user
        Post post = postService.findById(commentDTO.getPostId());
        User user = userService.findById(commentDTO.getUserId());
        comment.setPost(post);
        comment.setUser(user);

        // Set the parent comment if present
        if (commentDTO.getParentId() != null) {
            Comment parentComment = commentRepository.findById(commentDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParentComment(parentComment);
            // No need to manually update 'subComments' list of parentComment due to cascade settings
        }

        // Save the comment
        Comment commentSaved = commentRepository.save(comment);

        // Handle notification
        notificationService.createNotification(commentDTO.getUserId(), post.getUser().getId(), null, NotificationType.COMMENT);

        // Prepare and return DTO
        CommentDto commentDto = CommentDto.fromEntity(commentSaved);
        UserDto userDto = UserDto.toDto(comment.getUser());
        userDto.setProfileImageUrl(constructImageUrlForUser(comment.getUser()));
        commentDto.setUserDto(userDto);
        return commentDto;
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(UUID postId) {
        List<Comment> comments = commentRepository.findCommentsWithSubcommentsByPostId(postId);
        return comments.stream()
                .map(this::convertToCommentDtoRecursively)
                .collect(Collectors.toList());
    }


    public String constructImageUrlForUser(User user) {
        String baseUrl = "http://localhost:8081/images/";
        String imageName = user.getProfileImage();
        // Assuming the image name is based on the user's ID
        return baseUrl + user.getId() + '/' + imageName; // Adjust the file extension based on your actual image format
    }

    /**
     * Get a comment by id
     * @param commentId
     * @return CommentDto
     */
    public CommentDto getCommentById(UUID commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new CommentNotFoundException("Comment not found with id: " + commentId));
        return CommentDto.fromEntity(comment);
    }

    /**
     * Delete a comment by id
     * @param commentId
     */
    public void deleteComment(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new CommentNotFoundException("Comment not found with id: " + commentId));

        // Check if the user is authorized to delete the comment
        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedUserException("User not authorized to delete this comment");
        }

        // Recursive delete method to handle subcomments
        deleteCommentAndSubcomments(comment);
    }

    private void deleteCommentAndSubcomments(Comment comment) {
        // If comment has subcomments, delete them first
        if (comment.getSubComments() != null) {
            for (Comment subComment : comment.getSubComments()) {
                deleteCommentAndSubcomments(subComment);
            }
        }

        // Delete the comment itself
        commentRepository.delete(comment);
    }

    /**
     * Update a comment by id
     * @param commentId
     * @param updatedComment
     * @return CommentDto
     */
    public CommentDto updateComment(UUID commentId, UUID userId, CreateCommentDto updatedComment) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new CommentNotFoundException("Comment not found with id: " + commentId));

        // Check if the user updating the comment is the owner of the comment
        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedUserException("User not authorized to update this comment");
        }

        // Update the comment's value
        comment.setValue(updatedComment.getValue());

        // Save the updated comment
        Comment updated = commentRepository.save(comment);

        // Convert the updated comment entity to DTO
        return CommentDto.fromEntity(updated);
    }



    private CommentDto convertToCommentDtoRecursively(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setPostId(comment.getPost().getId());
        if (comment.getParentComment() != null) {
            commentDto.setParentId(comment.getParentComment().getId());
        }
        commentDto.setValue(comment.getValue());
        commentDto.setCreatedAt(comment.getCreatedAt());
        // Convert sub-comments recursively
        if (comment.getSubComments() != null) {
            commentDto.setSubComments(comment.getSubComments().stream()
                    .map(this::convertToCommentDtoRecursively)
                    .collect(Collectors.toList()));
        }
        // Set user DTO, including profile image URL
        UserDto userDto = UserDto.toDto(comment.getUser());
        userDto.setProfileImageUrl(constructImageUrlForUser(comment.getUser()));
        commentDto.setUserDto(userDto);
        return commentDto;
    }

    // Other methods
}

