package com.asusoftware.socialapp.user.model.dto;

import com.asusoftware.socialapp.user.model.User;
import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.Resource;

import java.util.UUID;

@Data
@Builder
public class UserPostDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String profileImage;

    public static UserPostDto fromEntity(User user) {
        return UserPostDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public static UserPostDto fromEntityList(User user) {
        return UserPostDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                //.imageUrl(user.getImageUrl())
                .build();
    }
}
