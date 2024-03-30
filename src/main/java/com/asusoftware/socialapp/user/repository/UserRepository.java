package com.asusoftware.socialapp.user.repository;

import com.asusoftware.socialapp.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByActivationCode(String activationCode);

    @Query(value = "SELECT * FROM users ORDER BY RANDOM() LIMIT 5", nativeQuery = true)
    List<User> findRandomUsers();


}
