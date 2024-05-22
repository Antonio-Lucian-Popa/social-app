package com.asusoftware.socialapp.story.service;

import com.asusoftware.socialapp.post.exception.StorageException;
import com.asusoftware.socialapp.story.model.Story;
import com.asusoftware.socialapp.story.model.dto.StoryDto;
import com.asusoftware.socialapp.story.repository.StoryRepository;
import com.asusoftware.socialapp.user.model.User;
import com.asusoftware.socialapp.user.model.dto.UserDto;
import com.asusoftware.socialapp.user.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Data
public class StoryService {

    @Value("${upload.dir}")
    private String uploadDir;

    @Value("${external-link.url}")
    private String externalImagesLink;

    private final UserService userService;
    private final StoryRepository storyRepository;

    public StoryService(UserService userService, StoryRepository storyRepository) {
        this.userService = userService;
        this.storyRepository = storyRepository;
    }

    public StoryDto saveStory(MultipartFile file, UUID userId) {
        if (file.isEmpty()) {
            throw new StorageException("Cannot store empty file.");
        }
        User user = userService.findById(userId);

        try {
            String originalFilename = file.getOriginalFilename();
            UUID storyId = UUID.randomUUID();

            Path directory = Paths.get(uploadDir, "stories", storyId.toString(), userId.toString()).toAbsolutePath().normalize();
            Files.createDirectories(directory);

            Path destinationFile = directory.resolve(originalFilename).normalize();
            if (!destinationFile.getParent().equals(directory)) {
                throw new StorageException("Cannot store file outside of the specified directory.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            String mediaUrl = Paths.get("stories", storyId.toString(), userId.toString(), originalFilename).toString().replace("\\", "/");
            LocalDateTime expirationDate = LocalDateTime.now().plus(24, ChronoUnit.HOURS);

            Story story = new Story();
            story.setValue(mediaUrl);
            story.setExpirationDate(expirationDate);
            story.setCreatedAt(LocalDateTime.now());
            story.setUser(user);

            story = storyRepository.save(story);

            StoryDto storyDto = StoryDto.toDto(story);
            storyDto.setValue(getImageFullUrl(mediaUrl));
            UserDto userDto = UserDto.toDto(user);
            userDto.setProfileImageUrl(userService.constructImageUrlForUser(user));
            storyDto.setUser(userDto);
            return storyDto;
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    private String getImageFullUrl(String mediaUrl) {
        System.out.println("MediaUrl: " + mediaUrl);
        return externalImagesLink + mediaUrl;
    }

    public List<StoryDto> getStoriesForUserAndFollowed(UUID userId) {
        List<Story> stories = storyRepository.findAllByUserOrUserFollowing(userId);
        return stories.stream().map(story -> {
            boolean viewed = story.getViewedBy().stream().anyMatch(user -> user.getId().equals(userId));
            List<UserDto> viewedBy = story.getViewedBy().stream()
                    .map(user -> {
                        UserDto userDto = UserDto.toDto(user);
                        userDto.setProfileImageUrl(userService.constructImageUrlForUser(user));
                        return userDto;
                    })
                    .collect(Collectors.toList());
            UserDto userDto = UserDto.toDto(story.getUser());
            userDto.setProfileImageUrl(userService.constructImageUrlForUser(story.getUser()));
            StoryDto storyDto = StoryDto.toDto(story);
            storyDto.setValue(getImageFullUrl(story.getValue()));
            storyDto.setViewed(viewed);
            storyDto.setViewedBy(viewedBy);
            storyDto.setUser(userDto);
            return storyDto;
        }).collect(Collectors.toList());
    }

    public List<StoryDto> getNewStories(UUID userId) {
        List<StoryDto> allStories = getStoriesForUserAndFollowed(userId);
        return allStories.stream().filter(story -> !story.isViewed()).collect(Collectors.toList());
    }

    public List<StoryDto> getSeenStories(UUID userId) {
        List<StoryDto> allStories = getStoriesForUserAndFollowed(userId);
        return allStories.stream().filter(StoryDto::isViewed).collect(Collectors.toList());
    }

    public void markStoryAsViewed(UUID storyId, UUID userId) {
        Story story = storyRepository.findById(storyId).orElseThrow(() -> new RuntimeException("Story not found"));
        User user = userService.findById(userId);
        story.getViewedBy().add(user);
        storyRepository.save(story);
    }
}
