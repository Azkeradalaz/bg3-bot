package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.baldursgate3.tgbot.bot.model.UserDto;

@Service
@RequiredArgsConstructor
public class UserService {
    private final RestTemplateService restTemplateService;

    public void registerUser(Long userId, String name) {
        restTemplateService.registerUser(userId, name);
    }

    public String getUserName(Long userId) {
        return restTemplateService.getUserByTgId(userId).name();
    }

    public UserDto getUserDto(Long userId) {
        return restTemplateService.getUserByTgId(userId);
    }
}
