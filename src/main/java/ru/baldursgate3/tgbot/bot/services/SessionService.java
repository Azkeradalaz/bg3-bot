package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.baldursgate3.tgbot.bot.enums.UserState;


@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final RestTemplateService restTemplateService;
    public void setSessionState(Long userId, UserState userState) {
        restTemplateService.updateSessionState(userId, userState.toString());
    }

    public Long getGameCharacterId(Long userId){
        return restTemplateService.getSession(userId).gameCharacterId();
    }

    public void setGameCharacterId(Long userId, Long gameCharacterId){
        restTemplateService.updateSessionGameCharacter(userId, gameCharacterId);;
    }
}