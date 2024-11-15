package ru.baldursgate3.tgbot.bot.states;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.baldursgate3.tgbot.bot.event.SendMessageEvent;
import ru.baldursgate3.tgbot.bot.services.MessageService;
import ru.baldursgate3.tgbot.bot.services.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainMenuState implements UserSessionState{
    private final UserService userService;
    private final MessageService messageService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void consumeMessage(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        SendMessage sendMessage = messageService.greetingRegisteredUser(userId,userService.getUserName(userId));
        applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
    }

    @Override
    public void consumeCallbackQuery(Update update) {

    }
}
