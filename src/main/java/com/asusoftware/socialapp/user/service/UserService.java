package com.asusoftware.socialapp.user.service;

import com.asusoftware.socialapp.auth.RegisterRequest;
import com.asusoftware.socialapp.exceptions.FileStorageException;
import com.asusoftware.socialapp.notification.model.NotificationType;
import com.asusoftware.socialapp.notification.service.NotificationService;
import com.asusoftware.socialapp.post.repository.PostRepository;
import com.asusoftware.socialapp.post.service.PostService;
import com.asusoftware.socialapp.user.exception.ImageNotFoundException;
import com.asusoftware.socialapp.user.exception.UserNotFoundException;
import com.asusoftware.socialapp.user.model.Role;
import com.asusoftware.socialapp.user.model.User;
import com.asusoftware.socialapp.user.model.dto.UpdateUserProfileDto;
import com.asusoftware.socialapp.user.model.dto.UserDto;
import com.asusoftware.socialapp.user.model.dto.UserProfileDto;
import com.asusoftware.socialapp.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Data
public class UserService {

    @Value("${upload.dir}")
    private String uploadDir;

    private final PostRepository postRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public UserProfileDto findByIdDto(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        long totalUserPosts = 0;

        String profileImageUrl = constructImageUrlForUser(user);
        UserProfileDto userProfileDto = UserProfileDto.toDto(user);
        userProfileDto.setProfileImageUrl(profileImageUrl);
        totalUserPosts = postRepository.countPostsByUserId(id);
        userProfileDto.setTotalPosts(totalUserPosts);


        return userProfileDto;
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

        notificationService.createNotification(follower.getId(), following.getId(), null, NotificationType.FOLLOW);

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
        // Find the user by ID
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }

        User user = userOptional.get();
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uploadPath = uploadDir + userId;

        try {
            // Ensure directory exists. This does not create a new directory if it already exists.
            Path uploadDirPath = Paths.get(uploadPath);
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }

            // If the user already has a profile image, delete the old file
            // Check if user.getProfileImage() is not null and not empty
            if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                Path oldFilePath = uploadDirPath.resolve(user.getProfileImage());
                if (Files.exists(oldFilePath)) {
                    Files.delete(oldFilePath); // Delete the old image
                }
            }

            // Copy the new file to the upload directory, replacing the existing file if it has the same name
            Path targetLocation = uploadDirPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Update the user's profile image with the new file name
            user.setProfileImage(fileName);
            userRepository.save(user);

            return fileName;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + fileName, e);
        }
    }

    public Resource getProfileImage(UUID userId) {
        User user = findById(userId);
        if (user == null || user.getProfileImage() == null) {
            throw new ImageNotFoundException("Profile image not found for user with ID: " + userId);
        }

        String filename = user.getProfileImage();
        Path filePath = Paths.get(uploadDir + userId).resolve(filename);

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ImageNotFoundException("Profile image not found for user with ID: " + userId);
            }
        } catch (MalformedURLException e) {
            throw new ImageNotFoundException("Profile image not found for user with ID: " + userId, e);
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


    public UpdateUserProfileDto updateUserProfile(UUID userId, UpdateUserProfileDto updatedUserDto, MultipartFile userProfileImage) {

        User findedUserRepo = userRepository.findById(userId).orElseThrow();

        findedUserRepo.setFirstName(updatedUserDto.getFirstName());
        findedUserRepo.setLastName(updatedUserDto.getLastName());
        findedUserRepo.setEmail(updatedUserDto.getEmail());
        findedUserRepo.setBirthday(updatedUserDto.getBirthday());
        findedUserRepo.setGender(updatedUserDto.getGender());
        findedUserRepo.setBio(updatedUserDto.getBio());
        findedUserRepo.setInterests(updatedUserDto.getInterests());
        findedUserRepo.setLivesIn(updatedUserDto.getLivesIn());

        userRepository.save(findedUserRepo);

        if (userProfileImage != null && !userProfileImage.isEmpty()) {
            uploadProfileImage(
                    userProfileImage,
                    findedUserRepo.getId()
            );
        }

        String profileImageUrl = constructImageUrlForUser(findedUserRepo);
        UpdateUserProfileDto userProfileDto = UpdateUserProfileDto.toDto(findedUserRepo);
        userProfileDto.setProfileImageUrl(profileImageUrl);
        return userProfileDto;
    }

    /**
     * Is used to load images in Front-end app from Back-end link to the folder of images
     * @param user the user which we want to see the image
     * @return return the url concatenation to view the image on the Front-end client
     */
    public String constructImageUrlForUser(User user) {
        String baseUrl = "http://localhost:8081/images/";

        String imageName = user.getProfileImage();
        // Assuming the image name is based on the user's ID
        return baseUrl + user.getId() + '/' + imageName; // Adjust the file extension based on your actual image format
    }

    // TODO: find random user that is not my user. So we need to retreive users but not our user
    public List<UserDto> findRandomUsers() {
        return userRepository.findRandomUsers().stream().map(user -> {
            String profileImageUrl = constructImageUrlForUser(user);
            UserDto userDto = UserDto.toDto(user);
            userDto.setProfileImageUrl(profileImageUrl);
            return userDto;
        }).collect(Collectors.toList());
    }

}
