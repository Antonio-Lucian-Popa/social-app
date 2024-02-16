package com.asusoftware.socialapp.post.model.dto;

import com.asusoftware.socialapp.post.model.Post;
import com.asusoftware.socialapp.user.model.dto.UserPostDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class PostDto {

    private UUID id;
    private String description;
    //private String imageUrl;
    private LocalDateTime createdAt;
    private UserPostDto user;
    private List<UserPostDto> userLikes;
    private int numberOfComments;
    private List<String> imageFilenames;

    public static PostDto fromEntityList(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .description(post.getDescription())
                //.imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .user(UserPostDto.fromEntity(post.getUser()))
                .userLikes(post.getUserLikes().stream().map(UserPostDto::fromEntityList).collect(Collectors.toList()))
                .build();
    }

    public static PostDto fromEntity(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .description(post.getDescription())
                .createdAt(post.getCreatedAt())
                .user(UserPostDto.fromEntity(post.getUser()))
                .userLikes(post.getUserLikes().stream().map(UserPostDto::fromEntity).collect(Collectors.toList()))
                .imageFilenames(post.getImageFilenames()) // Add this line
                .build();
    }
}
