package ru.baldursgate3.tgbot.bot.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.states.UserSessionState;

@Getter
public class UserSessionStateChangeEvent extends ApplicationEvent {

    private Long userId;
    private UserState userState;
    public UserSessionStateChangeEvent(Object source, Long userId, UserState userState) {
        super(source);
        this.userId = userId;
        this.userState = userState;
    }
}
