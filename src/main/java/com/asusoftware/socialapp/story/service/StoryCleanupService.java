package com.asusoftware.socialapp.story.service;

import com.asusoftware.socialapp.post.exception.StorageException;
import com.asusoftware.socialapp.story.model.Story;
import com.asusoftware.socialapp.story.repository.StoryRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Data
@Service
public class StoryCleanupService {

    @Value("${upload.dir}")
    private String uploadDir;

    private final StoryRepository storyRepository;

    @Scheduled(cron = "0 0 * * * *") // Run this task every hour
    public void removeExpiredStories() {
        List<Story> expiredStories = storyRepository.findAllByExpirationDateBefore(LocalDateTime.now());
        for (Story story : expiredStories) {
            removeStoryFiles(story);
            storyRepository.delete(story);
        }
    }

    private void removeStoryFiles(Story story) {
        try {
            Path directory = Paths.get(uploadDir, "stories", story.getId().toString(), story.getUser().getId().toString()).toAbsolutePath().normalize();
            if (Files.exists(directory)) {
                Files.walk(directory)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to delete story files.", e);
        }
    }
}
