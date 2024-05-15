package com.asusoftware.socialapp.story.controller;

import com.asusoftware.socialapp.story.model.dto.StoryDto;
import com.asusoftware.socialapp.story.service.StoryService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
@RestController
@RequestMapping("/api/v1/stories")
public class StoryController {

    private final StoryService storyService;

    @PostMapping(path = "/{userId}")
    public StoryDto addStory(@PathVariable(name = "userId") UUID userId, @RequestParam("file") MultipartFile storyValue) {
        return storyService.saveStory(storyValue, userId);
    }

    @GetMapping("/new")
    public List<StoryDto> getNewStories(@RequestParam UUID userId) {
        return storyService.getNewStories(userId);
    }

    @GetMapping("/seen")
    public List<StoryDto> getSeenStories(@RequestParam UUID userId) {
        return storyService.getSeenStories(userId);
    }

    @PostMapping("/view/{storyId}")
    public void markStoryAsViewed(@PathVariable UUID storyId, @RequestParam UUID userId) {
        storyService.markStoryAsViewed(storyId, userId);
    }
}
