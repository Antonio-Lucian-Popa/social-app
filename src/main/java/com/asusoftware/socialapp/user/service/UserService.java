package com.asusoftware.socialapp.user.service;

import com.asusoftware.socialapp.exceptions.FileStorageException;
import com.asusoftware.socialapp.user.exception.UserNotFoundException;
import com.asusoftware.socialapp.user.model.User;
import com.asusoftware.socialapp.user.model.dto.UserProfileDto;
import com.asusoftware.socialapp.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Data
public class UserService {

    @Value("${upload.dir}")
    private String uploadDir;

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

    public String uploadProfileImage(MultipartFile file, UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }

        User user = userOptional.get();
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uploadPath = uploadDir + userId;

        try {
            Files.createDirectories(Paths.get(uploadPath));
            Files.copy(file.getInputStream(), Paths.get(uploadPath).resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            user.setProfileImage(fileName);
            userRepository.save(user);
            return fileName;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + fileName, e);
        }
    }

    public void deleteProfileImage(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }

        User user = userOptional.get();
        String fileName = user.getProfileImage();
        if (fileName == null || fileName.isEmpty()) {
            throw new FileStorageException("No profile image to delete for user: " + userId);
        }

        String uploadPath = uploadDir + userId;

        try {
            Path fileToDeletePath = Paths.get(uploadPath).resolve(fileName);
            Files.deleteIfExists(fileToDeletePath);
            user.setProfileImage(null);
            userRepository.save(user);
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete file: " + fileName, e);
        }
    }
    public String updateProfileImage(MultipartFile file, UUID userId) {
        // Delete the existing image
        deleteProfileImage(userId);

        // Upload the new image
        return uploadProfileImage(file, userId);
    }




}
