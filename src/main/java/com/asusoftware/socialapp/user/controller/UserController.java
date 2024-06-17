package com.asusoftware.socialapp.user.controller;

import com.asusoftware.socialapp.auth.RegisterRequest;
import com.asusoftware.socialapp.user.exception.ImageNotFoundException;
import com.asusoftware.socialapp.user.model.User;
import com.asusoftware.socialapp.user.model.dto.ChangePasswordRequest;
import com.asusoftware.socialapp.user.model.dto.UpdateUserProfileDto;
import com.asusoftware.socialapp.user.model.dto.UserDto;
import com.asusoftware.socialapp.user.model.dto.UserProfileDto;
import com.asusoftware.socialapp.user.service.UserService;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@RestController
@RequestMapping(path = "api/v1/users")
public class UserController {

    @Value("${upload.dir}")
    private String uploadDir;

    private final UserService userService;

    @PutMapping(path = "/{followerId}/follow/{followingId}")
    public void followUser(@PathVariable(name = "followerId") UUID followerId, @PathVariable(name = "followingId") UUID followingId) {
        userService.followUser(followerId, followingId);
    }

    @PutMapping(path = "/{followerId}/unfollow/{followingId}")
    public void unfollowUser(@PathVariable(name = "followerId") UUID followerId, @PathVariable(name = "followingId") UUID followingId) {
        userService.unfollowUser(followerId, followingId);
    }

    @PutMapping(path = "/{userId}/update")
    public ResponseEntity<UpdateUserProfileDto> updateUserProfile(@PathVariable(name = "userId") UUID userId,
                                  @RequestPart("request") UpdateUserProfileDto updatedUserDto,
                                  @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResponseEntity.ok(userService.updateUserProfile(userId, updatedUserDto, file));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<UserProfileDto> findById(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(userService.findByIdDto(id));
    }

    @GetMapping("/suggested-users/{userId}")
    public ResponseEntity<List<UserDto>> getRandomUsers(@PathVariable(name = "userId") UUID userId) {
        // TODO: get random users that there are not in the current user following
        return ResponseEntity.ok(userService.findRandomUsers(userId));
    }

    @GetMapping("/search/users")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String name) {
        return ResponseEntity.ok(userService.searchUsersByName(name));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") UUID userId
    ) {
        String filename = userService.uploadProfileImage(file, userId);
        return ResponseEntity.ok("File uploaded: " + filename);
    }

    @PostMapping("/{userId}/updateProfileImage")
    public ResponseEntity<String> updateProfileImage(@RequestParam("file") MultipartFile file, @PathVariable UUID userId) {
        String fileName = userService.updateProfileImage(file, userId);
        return ResponseEntity.ok(fileName);
    }

    @PostMapping("/check")
    public List<UUID> checkIfFollowing(@RequestParam UUID followerId, @RequestBody List<UUID> followedIds) {
        return userService.checkIfFollowing(followerId, followedIds);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request.getUserId(), request.getOldPassword(), request.getNewPassword());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password has been changed");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/deleteProfileImage")
    public ResponseEntity<String> deleteProfileImage(@PathVariable UUID userId) {
        userService.deleteProfileImage(userId);
        return ResponseEntity.ok("Profile image deleted successfully");
    }

    @GetMapping("/image/{userId}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable UUID userId) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // Adjust the content type as needed
                .body(userService.getProfileImage(userId));
    }

    @GetMapping("/{userId}/followers")
    public List<UserDto> getFollowers(@PathVariable(name = "userId") UUID userId) {
        return userService.getFollowers(userId);
    }

    @GetMapping("/{userId}/following")
    public List<UserDto> getFollowing(@PathVariable(name = "userId") UUID userId) {
        return userService.getFollowing(userId);
    }
}
