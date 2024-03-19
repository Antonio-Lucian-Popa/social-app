package com.asusoftware.socialapp.post.controller;

import com.asusoftware.socialapp.post.model.dto.CreatePostDto;
import com.asusoftware.socialapp.post.model.dto.PostDto;
import com.asusoftware.socialapp.post.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    /*
    @PostMapping(path = "/create/{userId}")
    public void createPost(@RequestBody CreatePostDto createPostDto, @PathVariable("userId") UUID userId) {
        postService.createPost(createPostDto, userId);
    } */

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
    public ResponseEntity<Page<PostDto>> findFollowingPostsByUserId(
            @PathVariable("userId") UUID userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PostDto> postDtos = postService.findAllFollowingUsersPosts(userId, pageable);
        return ResponseEntity.ok(postDtos);
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


    @PutMapping(path = "/like/{postId}/{userId}")
    public void likePost(@PathVariable("postId") UUID postId, @PathVariable("userId") UUID userId) {
        postService.likePost(postId, userId);
    }

    @PutMapping(path = "/unlike/{postId}/{userId}")
    public void unlikePost(@PathVariable("postId") UUID postId, @PathVariable("userId") UUID userId) {
        postService.unlikePost(postId, userId);
    }

    @DeleteMapping(path = "/delete/{id}/{userId}")
    public void deletePostById(@PathVariable("id") UUID id, @PathVariable("userId") UUID userId) {
        postService.deletePostById(id, userId);
    }
}
