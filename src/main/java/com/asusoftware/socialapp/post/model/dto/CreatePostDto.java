package com.asusoftware.socialapp.post.model.dto;

import com.asusoftware.socialapp.post.model.Post;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class CreatePostDto {

    private String description;
    //private String imageUrl;

    public static Post toEntity(CreatePostDto createPostDto) {
        return Post.builder()
                .description(createPostDto.getDescription())
                .createdAt(LocalDateTime.now())
                //.imageUrl(imageUrl)
                .build();
    }
}
