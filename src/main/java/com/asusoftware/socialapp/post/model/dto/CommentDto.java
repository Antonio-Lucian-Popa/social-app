package com.asusoftware.socialapp.post.model.dto;

import com.asusoftware.socialapp.post.model.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CommentDto {
    private UUID id;
    private String value;
    private LocalDateTime createdAt;

    public static CommentDto fromEntity(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setValue(comment.getValue());
        commentDto.setCreatedAt(comment.getCreatedAt());
        return commentDto;
    }

    public static List<CommentDto> fromEntityList(List<Comment> comments) {
        return comments.stream().map(CommentDto::fromEntity).toList();
    }
}
