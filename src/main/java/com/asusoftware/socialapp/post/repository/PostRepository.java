package com.asusoftware.socialapp.post.repository;

import com.asusoftware.socialapp.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    // Alte metode specifice postării
    List<Post> findByUserId(UUID userId);

    Post findByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT p FROM Post p WHERE p.user IN (SELECT u.following FROM User u WHERE u.id = :userId)")
    Page<Post> findFollowingUsersPosts(UUID userId, Pageable pageable);

    Page<Post> findByUserId(UUID userId, Pageable pageable);

}
