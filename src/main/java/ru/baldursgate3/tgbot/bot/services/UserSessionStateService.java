package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.baldursgate3.tgbot.bot.model.Session;
import ru.baldursgate3.tgbot.bot.states.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionStateService {
    private final UserRegisterState userRegisterState;
    private final MainMenuState mainMenuState;
    private final CharacterListState characterListState;
    private final CharacterEditState characterEditState;
    private final UserService userService;
    private final Map<Long, Session> sessionMap = new HashMap<>();

    public UserSessionState getSessionState(Long userId, Long chatId) {
        if (userService.isRegistered(userId)) {
            if (sessionMap.get(userId) == null) {
                putSession(userId, chatId, mainMenuState, null, null);//todo полная хрень, разобраться с датами. скорее всего поставить таймстампы
            }
        } else {
            putSession(userId, chatId, userRegisterState, null, null);//todo полная хрень, разобраться с датами. скорее всего поставить таймстампы\
        }
        Session session = sessionMap.get(userId);
        return session.getUserSessionState();
    }

    public void setMainMenuState(Long userId) {
        sessionMap.get(userId).setUserSessionState(mainMenuState);
    }
    public void setCharacterListState(Long userId){
        sessionMap.get(userId).setUserSessionState(characterListState);
    }
    public void setCharacterEditState(Long userId){
        sessionMap.get(userId).setUserSessionState(characterEditState);
    }
    private void putSession(Long userId, Long chatId, UserSessionState userSessionState, Date dateCreated, Date dateExpire) {
        sessionMap.put(userId, new Session(chatId, userSessionState, dateCreated, dateExpire));
    }
}
