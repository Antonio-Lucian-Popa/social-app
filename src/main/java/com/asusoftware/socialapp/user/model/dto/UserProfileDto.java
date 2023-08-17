package com.asusoftware.socialapp.user.model.dto;

import com.asusoftware.socialapp.user.model.Gender;
import com.asusoftware.socialapp.user.model.User;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class UserProfileDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private Date birthday;
    private Gender gender;
    private List<UserDto> following;
    private List<UserDto> followers;

    public static UserProfileDto toDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setBirthday(user.getBirthday());
        dto.setGender(user.getGender());
        dto.setFollowing(user.getFollowing().stream().map(UserDto::toDto).collect(Collectors.toList()));
        dto.setFollowers(user.getFollowers().stream().map(UserDto::toDto).collect(Collectors.toList()));
        return dto;
    }
}
