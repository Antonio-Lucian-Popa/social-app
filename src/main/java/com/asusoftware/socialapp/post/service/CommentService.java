package com.asusoftware.socialapp.post.service;

import com.asusoftware.socialapp.post.exception.CommentNotFoundException;
import com.asusoftware.socialapp.post.model.Comment;
import com.asusoftware.socialapp.post.model.Post;
import com.asusoftware.socialapp.post.model.dto.CommentDto;
import com.asusoftware.socialapp.post.model.dto.CreateCommentDto;
import com.asusoftware.socialapp.post.repository.CommentRepository;
import com.asusoftware.socialapp.user.model.User;
import com.asusoftware.socialapp.user.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        // Setting the creation time
        comment.setCreatedAt(LocalDateTime.now());

        // Set the post and user - assuming you have methods to fetch them
        // For example, using a PostService and UserService
        Post post = postService.findById(commentDTO.getPostId());
        User user = userService.findById(commentDTO.getUserId());
        comment.setPost(post);
        comment.setUser(user);

        // Set the parent comment for subComments, if applicable
        if (commentDTO.getParentId() != null) {
            Comment parentComment = commentRepository.findById(commentDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParentComment(parentComment);
            // TODO: remember to do the delete on the delete req of subcomment
           // parentComment.getSubComments().add(comment);
           // commentRepository.save(parentComment);
        }

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
   /* public List<CommentDto> getCommentsByPostId(UUID postId) {
        List<Comment> comments = commentRepository.findCommentsWithSubcommentsByPostId(postId);
        System.out.println(comments);
        return CommentDto.fromEntityList(comments);
    } */

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(UUID postId) {
        List<Comment> comments = commentRepository.findCommentsWithSubcommentsByPostId(postId);
        return comments.stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
    }

    // Other methods

    // Add a subcomment to a comment
    @Transactional
    public CommentDto addSubComment(UUID parentId, CreateCommentDto subComment) {

        // Find the parent comment for subComment
        Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent comment not found with id " + parentId));

        // set the parent comment for this subComment
        subComment.setParentId(parentComment.getId());

        // find the user that commented this subComment
        User userComment = userService.findById(subComment.getUserId());

        // find the post entity where the subComment is placed
        Post post = postService.findById(subComment.getPostId());

        // convert subComment in entity to save
        Comment subCommentEntity = subComment.toEntity(subComment);

        // set the user that comments
        subCommentEntity.setUser(userComment);

        subCommentEntity.setPost(post);
        return CommentDto.fromEntity(commentRepository.save(subCommentEntity));
    }

    // Get all subcomments of a comment
    public List<Comment> getSubComments(UUID parentId) {
        return commentRepository.findAllByParentCommentId(parentId);
    }
}

