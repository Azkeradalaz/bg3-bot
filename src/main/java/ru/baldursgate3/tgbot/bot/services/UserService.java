package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final RestTemplateService restTemplateService;

    public boolean isNotRegistered(Long userId) {
        return restTemplateService.getUserByTgId(userId) == null;
    }

    public String registerUser(Long userId, String name) {
        return restTemplateService.registerUser(userId, name);
    }

    public String getUserName(Long userId) {
        return restTemplateService.getUserByTgId(userId).getName();
    }

}
