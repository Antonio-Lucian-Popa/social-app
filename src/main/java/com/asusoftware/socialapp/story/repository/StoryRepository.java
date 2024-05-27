package com.asusoftware.socialapp.story.repository;

import com.asusoftware.socialapp.story.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StoryRepository extends JpaRepository<Story, UUID> {

    @Query("SELECT s FROM Story s WHERE s.user.id = :userId OR s.user.id IN (SELECT f.id FROM User u JOIN u.following f WHERE u.id = :userId)")
    List<Story> findAllByUserOrUserFollowing(UUID userId);

    @Query("SELECT s FROM Story s WHERE s.expirationDate < :now")
    List<Story> findAllByExpirationDateBefore(@Param("now") LocalDateTime now);
}
