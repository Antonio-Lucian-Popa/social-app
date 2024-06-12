package com.asusoftware.socialapp.user.repository;

import com.asusoftware.socialapp.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByActivationCode(String activationCode);

    @Query(value = "SELECT * FROM users WHERE id <> :userId AND id NOT IN (SELECT user_id FROM user_followers WHERE follower_id = :userId) ORDER BY RANDOM() LIMIT 5", nativeQuery = true)
    List<User> findRandomUsers(UUID userId);


    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT(:name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT(:name, '%'))")
    List<User> findByUsernameStartingWith(String name);

    @Query("SELECT u FROM User u JOIN u.followers f WHERE f.id = :followerId AND u.id IN :followedIds")
    List<User> findIfFollowing(@Param("followerId") UUID followerId, @Param("followedIds") List<UUID> followedIds);

    @Query("SELECT f FROM User u JOIN u.followers f WHERE u.id = :userId")
    List<User> findFollowersByUserId(@Param("userId") UUID userId);


    @Query("SELECT f FROM User u JOIN u.following f WHERE u.id = :userId")
    List<User> findFollowingByUserId(@Param("userId") UUID userId);

}
