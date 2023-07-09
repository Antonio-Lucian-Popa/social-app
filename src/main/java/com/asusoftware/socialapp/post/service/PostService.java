package com.asusoftware.socialapp.post.service;

import com.asusoftware.socialapp.post.exception.PostNotFoundException;
import com.asusoftware.socialapp.post.model.Post;
import com.asusoftware.socialapp.post.model.dto.CreatePostDto;
import com.asusoftware.socialapp.post.model.dto.PostDto;
import com.asusoftware.socialapp.post.repository.PostRepository;
import com.asusoftware.socialapp.user.exception.UserNotFoundException;
import com.asusoftware.socialapp.user.model.User;
import com.asusoftware.socialapp.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    public void createPost(CreatePostDto createPostDto, UUID userId) {
        // retrieve user from database
        User user = userService.findById(userId);
        // create post
        Post post = createPostDto.toEntity();
        post.setUser(user);
        // save post
        postRepository.save(post);
    }

   public List<PostDto> findPostsByUserId(UUID userId) {
        List<Post> posts = postRepository.findByUserId(userId);
        return posts.stream().map(PostDto::fromEntityList).collect(Collectors.toList());
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
