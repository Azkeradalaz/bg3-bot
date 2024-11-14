package ru.baldursgate3.tgbot.bot.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.baldursgate3.tgbot.bot.states.UserSessionState;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class Session {
    @Setter(AccessLevel.NONE)
    Long chatId;
    UserSessionState userSessionState;
    @Setter(AccessLevel.NONE)
    Date dateCreated;
    @Setter(AccessLevel.NONE)
    Date dateExpire;

}
