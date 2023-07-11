package com.asusoftware.socialapp.user.controller;

import com.asusoftware.socialapp.user.model.dto.UserProfileDto;
import com.asusoftware.socialapp.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping(path = "api/v1/users")
public class UserController {

    private final UserService userService;

    @PutMapping(path = "/{followerId}/follow/{followingId}")
    public void followUser(@PathVariable(name = "followerId") UUID followerId, @PathVariable(name = "followingId") UUID followingId) {
        userService.followUser(followerId, followingId);
    }

    @PutMapping(path = "/{followerId}/unfollow/{followingId}")
    public void unfollowUser(@PathVariable(name = "followerId") UUID followerId, @PathVariable(name = "followingId") UUID followingId) {
        userService.unfollowUser(followerId, followingId);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<UserProfileDto> findById(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(UserProfileDto.toDto(userService.findById(id)));
    }
}
