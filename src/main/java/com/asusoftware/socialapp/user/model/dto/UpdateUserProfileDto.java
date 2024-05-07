package com.asusoftware.socialapp.user.model.dto;

import com.asusoftware.socialapp.user.model.Gender;
import com.asusoftware.socialapp.user.model.User;
import lombok.Data;

import java.util.Date;
import java.util.List;
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
    private String bio;
    private List<String> interests;
    private String livesIn;
    private boolean isUserNew;

    public static UpdateUserProfileDto toDto(User user) {
        UpdateUserProfileDto dto = new UpdateUserProfileDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
       // dto.profileImageUrl(user.getProfileImage());
        dto.setEmail(user.getEmail());
        dto.setBirthday(user.getBirthday());
        dto.setGender(user.getGender());
        dto.setBio(user.getBio());
        dto.setInterests(user.getInterests());
        dto.setLivesIn(user.getLivesIn());
        dto.setUserNew(user.isUserNew());
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
        user.setUserNew(userProfileDto.isUserNew());
        return user;
    }
}
