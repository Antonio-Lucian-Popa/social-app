package com.asusoftware.socialapp.post.controller;

import com.asusoftware.socialapp.post.model.Comment;
import com.asusoftware.socialapp.post.model.dto.CommentDto;
import com.asusoftware.socialapp.post.model.dto.CreateCommentDto;
import com.asusoftware.socialapp.post.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    /**
     * Create comment
     * @param commentDto comment dto
     * @return comment
     */
    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody CreateCommentDto commentDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(commentDto));
    }

    /**
     * Get comment by id
     * @param commentId comment id
     * @return comment
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable UUID commentId) {
        return ResponseEntity.ok(commentService.getCommentById(commentId));
    }

    /**
     * Get all comments by post id
     * @param postId post id
     * @return list of comments
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable UUID postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    /**
     * Delete comment by id
     * @param commentId comment id
     * @return no content
     */
    @DeleteMapping("/{commentId}/{userId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId, @PathVariable(name = "userId") UUID userId) {
        //TODO: permit also to admin of the post to delete comment, and not olny the user that created the comment
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update comment by id
     * @param commentId comment id
     * @param updatedComment updated comment
     * @return updated comment
     */
    @PutMapping("/{commentId}/{userId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable UUID commentId, @PathVariable(name = "userId") UUID userId, @RequestBody CreateCommentDto updatedComment) {
        return ResponseEntity.ok(commentService.updateComment(commentId, userId, updatedComment));
    }

    // Other endpoints and methods

    /*
    @PostMapping("/create-subcomment/{parentCommentId}")
    public ResponseEntity<Comment> createSubcomment(
            @PathVariable UUID parentCommentId,
            @RequestBody CreateSubcommentRequest request
    ) {
        Comment subcomment = commentService.createSubcomment(request.getValue(), parentCommentId, request.getUser());
        if (subcomment != null) {
            return new ResponseEntity<>(subcomment, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    } */
}

