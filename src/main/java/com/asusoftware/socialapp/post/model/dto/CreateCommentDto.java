package com.asusoftware.socialapp.post.model.dto;

import com.asusoftware.socialapp.post.model.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class CreateCommentDto {
    private String value;

    public Comment toEntity(CreateCommentDto commentDto) {
        Comment comment = new Comment();
        comment.setValue(commentDto.getValue());
        comment.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        return comment;
    }
}
