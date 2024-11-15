package ru.baldursgate3.tgbot.bot.states;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.baldursgate3.tgbot.bot.services.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegisterState implements UserSessionState{
    private final UserService userService;

    @Override
    public void consumeMessage(Update update) {

    }

    @Override
    public void consumeCallbackQuery(Update update) {

    }
}
