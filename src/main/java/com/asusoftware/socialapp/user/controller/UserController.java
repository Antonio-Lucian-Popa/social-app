package com.asusoftware.socialapp.user.controller;

import com.asusoftware.socialapp.user.exception.ImageNotFoundException;
import com.asusoftware.socialapp.user.model.User;
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

    @GetMapping(path = "/{id}")
    public ResponseEntity<UserProfileDto> findById(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(UserProfileDto.toDto(userService.findById(id)));
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

    @DeleteMapping("/{userId}/deleteProfileImage")
    public ResponseEntity<String> deleteProfileImage(@PathVariable UUID userId) {
        userService.deleteProfileImage(userId);
        return ResponseEntity.ok("Profile image deleted successfully");
    }

    @GetMapping("/image/{userId}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable UUID userId) {
        User user = userService.findById(userId);
        if (user == null || user.getProfileImage() == null) {
            throw new ImageNotFoundException("Profile image not found for user with ID: " + userId);
        }

        String filename = user.getProfileImage();
        Path filePath = Paths.get(uploadDir + userId).resolve(filename);

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Adjust the content type as needed
                        .body(resource);
            } else {
                throw new ImageNotFoundException("Profile image not found for user with ID: " + userId);
            }
        } catch (MalformedURLException e) {
            throw new ImageNotFoundException("Profile image not found for user with ID: " + userId, e);
        }
    }
}
