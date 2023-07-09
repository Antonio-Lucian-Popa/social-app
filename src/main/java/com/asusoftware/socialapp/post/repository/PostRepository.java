package com.asusoftware.socialapp.post.repository;

import com.asusoftware.socialapp.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    // Alte metode specifice postării
    List<Post> findByUserId(UUID userId);

    Post findByIdAndUserId(UUID id, UUID userId);
}
