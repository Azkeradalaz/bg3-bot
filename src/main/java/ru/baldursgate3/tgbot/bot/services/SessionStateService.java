package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.baldursgate3.tgbot.bot.model.Session;
import ru.baldursgate3.tgbot.bot.states.AuthoriseOrRegisterState;
import ru.baldursgate3.tgbot.bot.states.MainMenuState;
import ru.baldursgate3.tgbot.bot.states.UserSessionState;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionStateService {
    private final AuthoriseOrRegisterState authoriseOrRegisterState;
    private final MainMenuState mainMenuState;
    private final Map<Long, Session> sessionMap = new HashMap<>();

    public Session getSession(Long userId, Long chatId) {
        if (sessionMap.get(userId) == null) {
            putSession(userId, chatId, mainMenuState, null, null);//todo полная хрень, разобраться с датами. скорее всего поставить таймстампы
        }
        Session session = sessionMap.get(userId);
        log.info("Получена сессия {} {} {} {}", session.getChatId(), session.getUserSessionState(), session.getDateCreated(), session.getDateExpire());
        return session;
    }

    public void putSession(Long userId, Long chatId, UserSessionState userSessionState, Date dateCreated, Date dateExpire) {
        sessionMap.put(userId, new Session(chatId, userSessionState, dateCreated, dateExpire));
    }
}
