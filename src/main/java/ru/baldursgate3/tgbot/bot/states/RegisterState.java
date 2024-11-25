package ru.baldursgate3.tgbot.bot.states;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.event.EditMessageTextEvent;
import ru.baldursgate3.tgbot.bot.event.SendMessageEvent;
import ru.baldursgate3.tgbot.bot.services.MessageService;
import ru.baldursgate3.tgbot.bot.services.SessionService;
import ru.baldursgate3.tgbot.bot.services.UserService;


@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterState implements SessionState {
    private final UserService userService;
    private final MessageService messageService;
    private final SessionService sessionService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void consumeMessage(Long userId, Long chatId, String message) {
        Long editMessageId = messageService.getEditMessage(chatId);
        userService.registerUser(userId, message);
        sessionService.setSessionState(userId, UserState.MAIN_MENU);
        applicationEventPublisher.publishEvent(new EditMessageTextEvent(
                this, messageService.backToMainMenuMessage(chatId, message, editMessageId)));
    }

    @Override
    public void consumeCallbackQuery(Long userId, Long chatId, String callData) {
        SendMessage sendMessage = messageService.unknownCommandMessage(chatId);
        applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
    }

    @Override
    public void sendDefaultMessage(Long userId, Long chatId) {
        log.info("Новый пользователь на регистрацию {}", userId);
        applicationEventPublisher.publishEvent(new SendMessageEvent(
                        this, messageService.greetingNonRegisteredUser(chatId)));
    }
}
