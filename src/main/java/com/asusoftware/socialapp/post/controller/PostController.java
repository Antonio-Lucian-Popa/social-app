package com.asusoftware.socialapp.post.controller;

import com.asusoftware.socialapp.post.model.dto.CreatePostDto;
import com.asusoftware.socialapp.post.model.dto.PostDto;
import com.asusoftware.socialapp.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/posts")
public class PostController {

    private final PostService postService;

    @PostMapping(path = "/create/{userId}")
    public void createPost(@RequestBody CreatePostDto createPostDto, @PathVariable("userId") UUID userId) {
        postService.createPost(createPostDto, userId);
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<List<PostDto>> findPostsByUserId(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(postService.findPostsByUserId(userId));
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
