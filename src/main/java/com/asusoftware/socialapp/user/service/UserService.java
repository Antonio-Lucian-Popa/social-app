package com.asusoftware.socialapp.user.service;

import com.asusoftware.socialapp.user.exception.UserNotFoundException;
import com.asusoftware.socialapp.user.model.User;
import com.asusoftware.socialapp.user.model.dto.UserProfileDto;
import com.asusoftware.socialapp.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public UserProfileDto findByIdDto(UUID id) {
        return UserProfileDto.toDto(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    public void followUser(UUID followerId, UUID followingId) {
        User follower = userRepository.findById(followerId).orElseThrow(() ->
                new UserNotFoundException("User not found with id: " + followerId));

        User following = userRepository.findById(followingId).orElseThrow(() ->
                new UserNotFoundException("User not found with id: " + followingId));

        follower.getFollowing().add(following);
        following.getFollowers().add(follower);

        userRepository.save(follower);
        userRepository.save(following);
    }

    public void unfollowUser(UUID followerId, UUID followingId) {
        User follower = userRepository.findById(followerId).orElseThrow(() ->
                new UserNotFoundException("User not found with id: " + followerId));

        User following = userRepository.findById(followingId).orElseThrow(() ->
                new UserNotFoundException("User not found with id: " + followingId));

        follower.getFollowing().remove(following);
        following.getFollowers().remove(follower);

        userRepository.save(follower);
        userRepository.save(following);
    }


}
