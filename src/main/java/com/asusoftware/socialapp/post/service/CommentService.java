package com.asusoftware.socialapp.post.service;

import com.asusoftware.socialapp.post.exception.CommentNotFoundException;
import com.asusoftware.socialapp.post.exception.PostNotFoundException;
import com.asusoftware.socialapp.post.model.Comment;
import com.asusoftware.socialapp.post.model.Post;
import com.asusoftware.socialapp.post.model.dto.CommentDto;
import com.asusoftware.socialapp.post.model.dto.CreateCommentDto;
import com.asusoftware.socialapp.post.repository.CommentRepository;
import com.asusoftware.socialapp.user.model.User;
import com.asusoftware.socialapp.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
   // private final PostRepository postRepository;
    private final PostService postService;
    private final UserService userService;

    /**
     * Create a comment
     * @param postId
     * @param commentDto
     * @return CommentDto
     */
    public CommentDto createComment(UUID postId,UUID userId, CreateCommentDto commentDto) {
        Post post = postService.findById(postId);
        User user = userService.findById(userId);
        Comment comment = commentDto.toEntity(commentDto);
        comment.setUser(user);
        comment.setPost(post);
        return CommentDto.fromEntity(commentRepository.save(comment));
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
        if(!comment.getUser().getId().equals(userId)) {
            throw new CommentNotFoundException("Comment not found with id: " + commentId);
        } else {
            commentRepository.delete(comment);
        }
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
        if(!comment.getUser().getId().equals(userId)) {
            throw new CommentNotFoundException("Comment not found with id: " + commentId);
        } else {
            comment.setValue(updatedComment.getValue());
        }

        comment.setValue(updatedComment.getValue());
        return CommentDto.fromEntity(commentRepository.save(comment));
    }

    /**
     * Get all comments by post id
     * @param postId
     * @return List<CommentDto>
     */
    public List<CommentDto> getCommentsByPostId(UUID postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return CommentDto.fromEntityList(comments);
    }

    // Other methods

    /*
    @Transactional
    public SubcommentDTO createSubcomment(String value, UUID parentCommentId, User user) {
        Comment parentComment = commentRepository.findById(parentCommentId).orElse(null);
        if (parentComment == null) {
            // Handle the case where the parent comment does not exist
            return null;
        }

        Comment subcomment = new Comment();
        subcomment.setValue(value);
        subcomment.setPost(parentComment.getPost());
        subcomment.setUser(user);
        subcomment.setParentCommentId(parentCommentId);
        subcomment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(subcomment);

        return convertToSubcommentDTO(subcomment);
    }


    @Transactional(readOnly = true)
    public List<CommentDTO> getTopLevelComments() {
        List<Comment> topLevelComments = commentRepository.findByParentCommentIdIsNull();
        return topLevelComments.stream().map(this::convertToCommentDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SubcommentDTO> getSubcommentsByParentCommentId(UUID parentCommentId) {
        List<Comment> subcomments = commentRepository.findByParentCommentId(parentCommentId);
        return subcomments.stream().map(this::convertToSubcommentDTO).collect(Collectors.toList());
    }

    // Add more methods as needed, such as updating or deleting comments

    private CommentDTO convertToCommentDTO(Comment comment) {
        // Implement the conversion logic from Comment entity to CommentDTO here
    }

    private SubcommentDTO convertToSubcommentDTO(Comment comment) {
        // Implement the conversion logic from Comment entity to SubcommentDTO here
    } */
}

