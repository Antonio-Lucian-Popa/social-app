package com.asusoftware.socialapp.story.model.dto;

import com.asusoftware.socialapp.story.model.Story;
import com.asusoftware.socialapp.user.model.User;
import com.asusoftware.socialapp.user.model.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class StoryDto {


    private UUID id;
    private String value;
    private LocalDateTime expirationDate;
    private LocalDateTime createdAt;
    private UserDto user;
    private boolean viewed;
    private List<UserDto> viewedBy;

    public static Story toEntity(StoryDto storyDTO, User user) {
        Story story = new Story();
        story.setId(storyDTO.getId());
        story.setExpirationDate(storyDTO.getExpirationDate());
        story.setUser(user);

        return story;
    }

    public static StoryDto toDto(Story story) {
        StoryDto storyDto = new StoryDto();
        storyDto.setId(story.getId());
        storyDto.setExpirationDate(story.getExpirationDate());
        storyDto.setCreatedAt(story.getCreatedAt());
        return storyDto;
    }
}
