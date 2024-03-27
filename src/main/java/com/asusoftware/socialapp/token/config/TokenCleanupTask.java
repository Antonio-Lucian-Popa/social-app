package com.asusoftware.socialapp.token.config;

import com.asusoftware.socialapp.token.model.Token;
import com.asusoftware.socialapp.token.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TokenCleanupTask {

    @Autowired
    private TokenRepository tokenRepository;

    // Runs every day at 1 AM
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanupExpiredTokens() {
        List<Token> expiredTokens = tokenRepository.findByExpiredTrue();
        tokenRepository.deleteAll(expiredTokens);
    }
}
