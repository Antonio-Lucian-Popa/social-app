package com.asusoftware.socialapp.post.model.dto;

import com.asusoftware.socialapp.post.model.Post;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class CreatePostDto {

    private String description;
    //private String imageUrl;

    public Post toEntity() {
        return Post.builder()
                .description(description)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                //.imageUrl(imageUrl)
                .build();
    }
}
