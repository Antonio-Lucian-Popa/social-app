package com.asusoftware.socialapp.post.service;

import com.asusoftware.socialapp.notification.model.Notification;
import com.asusoftware.socialapp.notification.model.NotificationType;
import com.asusoftware.socialapp.notification.service.NotificationService;
import com.asusoftware.socialapp.post.exception.PostNotFoundException;
import com.asusoftware.socialapp.post.exception.StorageException;
import com.asusoftware.socialapp.post.model.Post;
import com.asusoftware.socialapp.post.model.dto.CreatePostDto;
import com.asusoftware.socialapp.post.model.dto.PostDto;
import com.asusoftware.socialapp.post.repository.PostRepository;
import com.asusoftware.socialapp.user.exception.UserNotFoundException;
import com.asusoftware.socialapp.user.model.User;
import com.asusoftware.socialapp.user.model.dto.UserPostDto;
import com.asusoftware.socialapp.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Data
public class PostService {

    @Value("${upload.dir}")
    private String uploadDir;

    private final PostRepository postRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public PostDto createPostWithImages(UUID userId, CreatePostDto createPostDto, List<MultipartFile> files) {
        User user = userService.findById(userId);
        Post post = CreatePostDto.toEntity(createPostDto);
        post.setUser(user);
        Post postSaved = postRepository.save(post); // This saves the post and gives it an ID

       if(files != null) {
           // Then, save the images and bind them to the post
           List<String> filenames = files.stream()
                   .map(file -> saveImage(file, postSaved.getId()))
                   .collect(Collectors.toList());

           // Update the Post entity with the image references
           postSaved.setImageFilenames(filenames);
       }
        postRepository.save(postSaved); // Update the post record with image references
        postSaved.setUserLikes(new HashSet<>());
        // Convert the updated Post entity to a DTO to return
        PostDto postDto = PostDto.fromEntity(postSaved);
        List<String> imageUrls = constructImageUrlsForPost(postSaved.getId());
        postDto.setImageFilenames(imageUrls); // Set image URLs for the post
        return postDto;
    }

    // TODO: de vazut de ce nu merge linkul catre post images ca la user

    private String saveImage(MultipartFile file, UUID postId) {
        if (file.isEmpty()) {
            throw new StorageException("Cannot store empty file.");
        }
        try {
            String originalFilename = file.getOriginalFilename();
            // Ensure the directory structure /posts/{postId}/ exists
            Path directory = Paths.get(uploadDir, "posts", postId.toString()).toAbsolutePath().normalize();
            Files.createDirectories(directory); // This will create all non-existent parent directories

            // Construct the final file path
            Path destinationFile = directory.resolve(originalFilename).normalize();

            // Security check to ensure we're not saving the file outside of the intended directory
            if (!destinationFile.getParent().equals(directory)) {
                throw new StorageException("Cannot store file outside of the specified directory.");
            }

            // Save the file
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            // Return the relative path to the saved file
            return Paths.get("posts", postId.toString(), originalFilename).toString();
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    public long countPostsByUserId(UUID userId) {
        return postRepository.countPostsByUserId(userId);
    }


    public Page<PostDto> findAllFollowingUsersPosts(UUID userId, Pageable pageable) {
        Page<Post> postPage = postRepository.findFollowingAndUsersPosts(userId, pageable);

        return postPage.map(post -> {
            UserPostDto userPostDto = UserPostDto.fromEntity(post.getUser());

            String userProfileImageUrl = constructImageUrlForUser(post.getUser().getId());
            userPostDto.setProfileImage(userProfileImageUrl); // Set user's profile image URL

            PostDto postDto = PostDto.fromEntity(post);
            postDto.setUser(userPostDto);
            postDto.setNumberOfComments(post.getComments().size());
            postDto.setCreatedAt(post.getCreatedAt());

            // Assuming each post can have multiple images, construct URLs for them
            List<String> imageUrls = constructImageUrlsForPost(post.getId());
            postDto.setImageFilenames(imageUrls); // Set image URLs for the post

            return postDto;
        });
    }

    private List<String> constructImageUrlsForPost(UUID postId) {
        String baseUrl = "http://localhost:8081/images/posts/";
        Path postImagesDir = Paths.get(uploadDir, "posts", postId.toString()).toAbsolutePath().normalize();

        List<String> imageUrls = new ArrayList<>();
        try {
            if (Files.exists(postImagesDir)) {
                Files.list(postImagesDir).forEach(filePath -> {
                    if (Files.isRegularFile(filePath)) {
                        // Construct a publicly accessible URL for each image
                        String imageUrl = baseUrl + postId + '/' + filePath.getFileName().toString();
                        imageUrls.add(imageUrl);
                    }
                });
            }
        } catch (IOException e) {
            // Log error or handle it according to your application's policies
            System.err.println("Error listing images for post: " + e.getMessage());
        }

        return imageUrls;
    }

    /**
     * Is used to load images in Front-end app from Back-end link to the folder of images
     * @param userId the user id which we want to see the image
     * @return return the url concatenation to view the image on the Front-end client
     */
    public String constructImageUrlForUser(UUID userId) {
        String baseUrl = "http://localhost:8081/images/";

        User user = userService.findById(userId);
        String imageName = user.getProfileImage();
        // Assuming the image name is based on the user's ID
        return baseUrl + userId + '/' + imageName; // Adjust the file extension based on your actual image format
    }


    public Page<PostDto> findPostsByUserId(UUID userId, Pageable pageable) {
        Page<Post> postPage = postRepository.findByUserId(userId, pageable);

        return postPage.map(post -> {
            UserPostDto userPostDto = UserPostDto.fromEntity(post.getUser());

            // Construct the URL for the user's profile image
            String userProfileImageUrl = constructImageUrlForUser(post.getUser().getId());
            userPostDto.setProfileImage(userProfileImageUrl);

            PostDto postDto = PostDto.fromEntity(post);
            postDto.setUser(userPostDto);
            postDto.setNumberOfComments(post.getComments().size());
            postDto.setCreatedAt(post.getCreatedAt());

            // Assuming each post can have multiple images, construct URLs for them
            List<String> imageUrls = constructImageUrlsForPost(post.getId());
            postDto.setImageFilenames(imageUrls); // Set image URLs for the post

            return postDto;
        });
    }


    public Post findById(UUID id) {
        return postRepository.findById(id).orElseThrow(() ->
                new PostNotFoundException("Post not found with id: " + id));
    }

    public void deletePostById(UUID id, UUID userId) {
        Post post = postRepository.findByIdAndUserId(id, userId);
        postRepository.delete(post);
    }

    @Transactional
    public PostDto likePost(UUID postId, UUID userId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostNotFoundException("Post not found with id: " + postId));

        User likingUser = userService.findById(userId);

        Set<User> likedPosts = post.getUserLikes();
        if (!likedPosts.contains(likingUser)) {
            likedPosts.add(likingUser);
            post.setUserLikes(likedPosts);
            postRepository.save(post);

            // if the current user likes his posts the notification doesn't need to trigger
            if(likingUser.getId() != post.getUser().getId()) {
                // Create and save the notification for the post owner
                notificationService.createNotification(likingUser.getId(), post.getUser().getId(), postId, NotificationType.LIKE);
            }
        }
        return PostDto.fromEntity(post);
    }

    public PostDto unlikePost(UUID postId, UUID userId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostNotFoundException("Post not found with id: " + postId));

        User user = userService.findById(userId);

        Set<User> likedPosts = post.getUserLikes();
        likedPosts.remove(user);

        post.setUserLikes(likedPosts);
        postRepository.save(post);
        return PostDto.fromEntity(post);
    }
}
