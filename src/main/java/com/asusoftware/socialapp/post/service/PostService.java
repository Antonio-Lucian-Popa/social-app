package com.asusoftware.socialapp.post.service;

import com.asusoftware.socialapp.post.exception.PostNotFoundException;
import com.asusoftware.socialapp.post.exception.StorageException;
import com.asusoftware.socialapp.post.model.Post;
import com.asusoftware.socialapp.post.model.dto.CreatePostDto;
import com.asusoftware.socialapp.post.model.dto.PostDto;
import com.asusoftware.socialapp.post.repository.PostRepository;
import com.asusoftware.socialapp.user.exception.UserNotFoundException;
import com.asusoftware.socialapp.user.model.User;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Data
public class PostService {

    @Value("${upload.dir}")
    private String uploadDir;

    private final PostRepository postRepository;
    private final UserService userService;

    /*
    public void createPost(CreatePostDto createPostDto, UUID userId) {
        // retrieve user from database
        User user = userService.findById(userId);
        // create post
        Post post = createPostDto.toEntity();
        post.setUser(user);
        // save post
        postRepository.save(post);
    } */

    @Transactional
    public PostDto createPostWithImages(UUID userId, CreatePostDto createPostDto, List<MultipartFile> files) {
        User user = userService.findById(userId);
        Post post = createPostDto.toEntity();
        post.setUser(user);
        Post postSaved = postRepository.save(createPostDto.toEntity()); // This saves the post and gives it an ID

        // Then, save the images and bind them to the post
        List<String> filenames = files.stream()
                .map(file -> saveImage(file, post.getId()))
                .collect(Collectors.toList());

        // Update the Post entity with the image references
        postSaved.setImageFilenames(filenames);
        postRepository.save(postSaved); // Update the post record with image references

        // Convert the updated Post entity to a DTO to return
        return PostDto.fromEntity(postSaved);
    }

    private String saveImage(MultipartFile file, UUID postId) {
        if (file.isEmpty()) {
            // Handle empty file case
        }
        try {
            String originalFilename = file.getOriginalFilename();
            String filename = postId.toString() + "_" + originalFilename;
            Path destinationFile = Paths.get(uploadDir).resolve(Paths.get(filename))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(Paths.get(uploadDir).toAbsolutePath())) {
                throw new StorageException("Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return filename;
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }


    public Page<PostDto> findAllFollowingUsersPosts(UUID userId, Pageable pageable) {
        Page<Post> postPage = postRepository.findFollowingUsersPosts(userId, pageable);
        return postPage.map(post -> {
            PostDto postDto = PostDto.fromEntityList(post);
            postDto.setNumberOfComments(post.getComments().size());
            return postDto;
        });
    }


    public Page<PostDto> findPostsByUserId(UUID userId, Pageable pageable) {
        Page<Post> postPage = postRepository.findByUserId(userId, pageable);

        return postPage.map(post -> {
            PostDto postDto = PostDto.fromEntity(post);
            postDto.setNumberOfComments(post.getComments().size());
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

    public void likePost(UUID postId, UUID userId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostNotFoundException("Post not found with id: " + postId));

        User user = userService.findById(userId);

        Set<User> likedPosts = post.getUserLikes();
        likedPosts.add(user);

        post.setUserLikes(likedPosts);
        postRepository.save(post);
    }

    public void unlikePost(UUID postId, UUID userId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostNotFoundException("Post not found with id: " + postId));

        User user = userService.findById(userId);

        Set<User> likedPosts = post.getUserLikes();
        likedPosts.remove(user);

        post.setUserLikes(likedPosts);
        postRepository.save(post);
    }
}
