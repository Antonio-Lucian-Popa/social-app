package com.asusoftware.socialapp.post.model.dto;

import com.asusoftware.socialapp.post.model.Comment;
import com.asusoftware.socialapp.user.model.dto.UserDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class CommentDto {
    private UUID id;
    private String value;
    private LocalDateTime createdAt;
    private UUID postId;
    private UserDto userDto;
    private UUID parentId;
    private List<CommentDto> subComments;

    public static CommentDto fromEntity(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setValue(comment.getValue());
        commentDto.setCreatedAt(comment.getCreatedAt());
        commentDto.setPostId(comment.getPost().getId());
        commentDto.setUserDto(UserDto.toDto(comment.getUser()));

        if (comment.getParentComment() != null) {
            commentDto.setParentId(comment.getParentComment().getId());
        }

        commentDto.setSubComments(fromEntityList(comment.getSubComments()));
        return commentDto;
    }


    public static List<CommentDto> fromEntityList(List<Comment> comments) {
        if(comments == null) return null; // (1)
        return comments.stream().map(CommentDto::fromEntity).collect(Collectors.toList());
    }
}
