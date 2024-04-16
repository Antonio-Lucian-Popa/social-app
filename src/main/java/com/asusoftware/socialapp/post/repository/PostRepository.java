package com.asusoftware.socialapp.post.repository;

import com.asusoftware.socialapp.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    // Alte metode specifice postării
    List<Post> findByUserId(UUID userId);

    Post findByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT p FROM Post p WHERE p.user IN (SELECT u.following FROM User u WHERE u.id = :userId) OR p.user.id = :userId")
    Page<Post> findFollowingAndUsersPosts(UUID userId, Pageable pageable);


    @Query("SELECT p FROM Post p WHERE p.user.id = :userId")
    Page<Post> findByUserId(UUID userId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.user.id = :userId")
    long countPostsByUserId(@Param("userId") UUID userId);

    @Query("SELECT p FROM Post p WHERE p.imageFilenames IS NOT EMPTY")
    Page<Post> findPostsWithImages(Pageable pageable);

}
