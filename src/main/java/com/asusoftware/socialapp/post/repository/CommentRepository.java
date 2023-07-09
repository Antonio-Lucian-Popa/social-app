package com.asusoftware.socialapp.post.repository;

import com.asusoftware.socialapp.post.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    // Alte metode specifice comentariului
}