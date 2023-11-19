package com.asusoftware.socialapp.post.model.dto;

import com.asusoftware.socialapp.post.model.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Data
public class CreateCommentDto {

    private UUID parentId; // Optional, only needed for subcomments
    private String value;
    private UUID postId;
    private UUID userId;

    public Comment toEntity(CreateCommentDto commentDto) {
        Comment comment = new Comment();
        comment.setValue(commentDto.getValue());
        comment.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        return comment;
    }
}
