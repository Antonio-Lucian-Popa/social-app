package com.asusoftware.socialapp.post.repository;

import com.asusoftware.socialapp.post.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByPostId(UUID postId);
    // Alte metode specifice comentariului

    List<Comment> findByParentCommentIdIsNull(); // Find top-level comments

    List<Comment> findByParentCommentId(UUID parentCommentId); // Find subcomments by parentCommentId

    List<Comment> findAllByParentCommentId(UUID parentId); // Find all subcomments by parentCommentId

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.subComments WHERE c.parentComment IS NULL AND c.post.id = :postId")
    List<Comment> findCommentsWithSubcommentsByPostId(@Param("postId") UUID postId);

}