package ru.baldursgate3.tgbot.bot.states;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.event.SendMessageEvent;
import ru.baldursgate3.tgbot.bot.services.MessageService;
import ru.baldursgate3.tgbot.bot.services.SessionService;



@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterStateOne implements SessionState {
    private final MessageService messageService;
    private final SessionService sessionService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void consumeMessage(Long userId, Long chatId, String message) {
        log.info("Новый пользователь на регистрацию {}", userId);
        sessionService.setSessionState(userId, UserState.REGISTER_STEP_TWO);
        applicationEventPublisher.publishEvent(new SendMessageEvent(this, messageService.greetingNonRegisteredUser(chatId)));
    }

    @Override
    public void consumeCallbackQuery(Long userId, Long chatId, String callData) {
        SendMessage sendMessage = messageService.unknownCommandMessage(chatId);
        applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));

    }
}
