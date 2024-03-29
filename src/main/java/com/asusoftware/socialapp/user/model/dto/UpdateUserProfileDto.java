package com.asusoftware.socialapp.user.model.dto;

import com.asusoftware.socialapp.user.model.Gender;
import com.asusoftware.socialapp.user.model.User;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class UpdateUserProfileDto {

    private UUID id;
    private String profileImageUrl;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Date birthday;
    private Gender gender;

    public static UpdateUserProfileDto toDto(User user) {
        UpdateUserProfileDto dto = new UpdateUserProfileDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
       // dto.profileImageUrl(user.getProfileImage());
        dto.setEmail(user.getEmail());
        dto.setBirthday(user.getBirthday());
        dto.setGender(user.getGender());
        return dto;
    }

    public static User toEntity(UserProfileDto userProfileDto) {
        User user = new User();
        user.setId(userProfileDto.getId());
        user.setFirstName(userProfileDto.getFirstName());
        user.setLastName(userProfileDto.getLastName());
        user.setEmail(userProfileDto.getEmail());
        user.setBirthday(userProfileDto.getBirthday());
        user.setGender(userProfileDto.getGender());
        return user;
    }
}
