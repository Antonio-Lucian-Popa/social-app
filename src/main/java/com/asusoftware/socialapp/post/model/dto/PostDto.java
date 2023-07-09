package com.asusoftware.socialapp.post.model.dto;

import com.asusoftware.socialapp.post.model.Post;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PostDto {

    private UUID id;
    private String description;
    //private String imageUrl;
    private LocalDateTime createdAt;

    public static PostDto fromEntityList(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .description(post.getDescription())
                //.imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
