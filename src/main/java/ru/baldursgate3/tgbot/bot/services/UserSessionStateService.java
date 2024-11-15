package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.event.UserSessionStateChangeEvent;
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
    private final GameCharacterEditState gameCharacterEditState;
    private final GameCharacterListState gameCharacterListState;
    private final UserService userService;
    private final Map<Long, Session> sessionMap = new HashMap<>();

    public UserSessionState getSessionState(Long userId, Long chatId) {
        UserSessionState userSessionState = null;
        if (userService.isRegistered(userId)) {
            if (sessionMap.get(userId) == null) {
                putSession(userId, chatId, UserState.MAIN_MENU, null, null);//todo полная хрень, разобраться с датами. скорее всего поставить таймстампы
            }
        } else {
            putSession(userId, chatId, UserState.REGISTER, null, null);//todo полная хрень, разобраться с датами. скорее всего поставить таймстампы\
        }
        Session session = sessionMap.get(userId);
        UserState state = session.getUserState();

        switch (state) {
            case REGISTER:
                userSessionState = userRegisterState;
                break;
            case MAIN_MENU:
                userSessionState = mainMenuState;
                break;
            case CHARACTER_LIST:
                userSessionState = gameCharacterListState;
                break;
            case CHARACTER_EDIT:
                userSessionState = gameCharacterEditState;
                break;
            default:
                break;
        }
        log.info("UserState {}", userSessionState.getClass().getSimpleName());
        return userSessionState;
    }

    @EventListener
    public void userSessionStateChangeEventHandler(UserSessionStateChangeEvent userSessionStateChangeEvent) {
        Session session = sessionMap.get(userSessionStateChangeEvent.getUserId());
        session.setUserState(userSessionStateChangeEvent.getUserState());
        log.info("{} UserSessionState changed to {}", userSessionStateChangeEvent.getUserId(),
                userSessionStateChangeEvent.getUserState());
    }

    private void putSession(Long userId, Long chatId, UserState userState, Date dateCreated, Date dateExpire) {
        sessionMap.put(userId, new Session(chatId, userState, dateCreated, dateExpire));
    }
}
