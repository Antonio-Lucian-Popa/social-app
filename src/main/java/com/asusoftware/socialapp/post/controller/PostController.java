package com.asusoftware.socialapp.post.controller;

import com.asusoftware.socialapp.post.model.dto.CreatePostDto;
import com.asusoftware.socialapp.post.model.dto.PostDto;
import com.asusoftware.socialapp.post.model.dto.PostImageDto;
import com.asusoftware.socialapp.post.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/posts")
public class PostController {

    @Value("${upload.dir}")
    private String uploadDir;
    private final PostService postService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<PostDto> createPostWithImages(
            @PathVariable("userId") UUID userId,
            @RequestPart("createPostDto") CreatePostDto createPostDto,
            @RequestPart(name = "files", required = false) List<MultipartFile> files
    ) {
        return ResponseEntity.ok(postService.createPostWithImages(userId, createPostDto, files));
    }

    /**
     * Find posts based on my user id, to retreive post from my friends
     * @param userId your user id
     * @param page
     * @param size
     * @return a list of posts based on page size
     */
    @GetMapping(path = "/findAllPosts/{userId}")
    public ResponseEntity<Page<PostDto>> findPostsByUserId(
            @PathVariable("userId") UUID userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "includeFollowing", defaultValue = "false") boolean includeFollowing) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PostDto> postDtos;

        if (includeFollowing) {
            postDtos = postService.findAllFollowingUsersPosts(userId, pageable);
        } else {
            postDtos = postService.findPostsByUserId(userId, pageable);
        }
        return ResponseEntity.ok(postDtos);
    }

    @GetMapping("/find-post/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(postService.findPostById(id));
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<Page<PostDto>> findPostsByUserId(
            @PathVariable("userId") UUID userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PostDto> postDtos = postService.findPostsByUserId(userId, pageable);
        return ResponseEntity.ok(postDtos);
    }

    @GetMapping("/popular-images")
    public ResponseEntity<Page<PostImageDto>> getFirstImageFromPosts(Pageable pageable) {
        Page<PostImageDto> images = postService.getFirstImageFromEachPost(pageable);
        return ResponseEntity.ok(images);
    }

    @PutMapping(path = "/update/{postId}/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDto> updatePost(@PathVariable("postId") UUID postId, @PathVariable("userId") UUID userId, @RequestPart("updatedPost") CreatePostDto updatePostDto, @RequestPart(name = "files", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(postService.updateExistingPost(postId, userId, updatePostDto, images));
    }

    @PutMapping(path = "/like/{postId}/{userId}")
    public ResponseEntity<PostDto> likePost(@PathVariable("postId") UUID postId, @PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(postService.likePost(postId, userId));
    }

    @PutMapping(path = "/unlike/{postId}/{userId}")
    public ResponseEntity<PostDto> unlikePost(@PathVariable("postId") UUID postId, @PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(postService.unlikePost(postId, userId));
    }

    @DeleteMapping(path = "/delete/{id}/{userId}")
    public void deletePostById(@PathVariable("id") UUID id, @PathVariable("userId") UUID userId) {
        postService.deletePostById(id, userId);
    }

    @DeleteMapping("/removeImages/{postId}")
    public ResponseEntity<?> removeImagesFromPost(@PathVariable UUID postId, @RequestBody List<String> imageUrls) {
        try {
            postService.removeImagesByUrls(postId, imageUrls);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error removing images: " + e.getMessage());
        }
    }
}
