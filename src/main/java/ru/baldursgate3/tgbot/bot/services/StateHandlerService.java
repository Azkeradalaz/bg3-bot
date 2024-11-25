package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.model.SessionDto;
import ru.baldursgate3.tgbot.bot.states.SessionState;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StateHandlerService {
    private final RestTemplateService restTemplateService;
    private final Map<UserState, SessionState> stateHandler;

    public SessionState getSessionState(Long userId) {
        SessionDto sessionDto = restTemplateService.getSession(userId);
        SessionState sessionState = stateHandler.get(UserState.valueOf(sessionDto.userState()));
        log.info("UserState {}", sessionState.getClass().getSimpleName());
        return sessionState;
    }
}
