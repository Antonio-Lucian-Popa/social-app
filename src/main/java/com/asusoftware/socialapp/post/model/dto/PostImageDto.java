package com.asusoftware.socialapp.post.model.dto;

import com.asusoftware.socialapp.post.model.Post;
import lombok.Data;

import java.util.UUID;

@Data
public class PostImageDto {
    private UUID postId;
    private String description;
    private String imageUrl;

    public static PostImageDto toDto(Post post) {
        PostImageDto imageDto = new PostImageDto();
        imageDto.setPostId(post.getId());
        imageDto.setDescription(post.getDescription());
        return imageDto;
    }

    public static PostImageDto toDto(Post post, String imageUrl) {
        PostImageDto imageDto = new PostImageDto();
        imageDto.setPostId(post.getId());
        imageDto.setDescription(post.getDescription());
        imageDto.setImageUrl(imageUrl);
        return imageDto;
    }

    // Getters and setters
}