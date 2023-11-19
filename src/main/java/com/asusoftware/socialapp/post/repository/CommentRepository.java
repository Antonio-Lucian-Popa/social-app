package com.asusoftware.socialapp.post.repository;

import com.asusoftware.socialapp.post.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByPostId(UUID postId);
    // Alte metode specifice comentariului

    List<Comment> findByParentCommentIdIsNull(); // Find top-level comments

    List<Comment> findByParentCommentId(UUID parentCommentId); // Find subcomments by parentCommentId
}