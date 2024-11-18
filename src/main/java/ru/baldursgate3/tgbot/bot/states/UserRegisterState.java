package ru.baldursgate3.tgbot.bot.states;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.event.EditMessageTextEvent;
import ru.baldursgate3.tgbot.bot.event.SendMessageEvent;
import ru.baldursgate3.tgbot.bot.event.UserSessionStateChangeEvent;
import ru.baldursgate3.tgbot.bot.services.MessageService;
import ru.baldursgate3.tgbot.bot.services.UserService;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisterState implements UserSessionState {
    private final UserService userService;
    private final MessageService messageService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Set<Long> toRegister = new HashSet<>();

    @Override
    public void consumeMessage(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        Long editMessageId = messageService.getEditMessage(chatId);

        if (toRegister.contains(userId)) {
            toRegister.remove(userId);
            userService.registerUser(userId, update.getMessage().getText());
            applicationEventPublisher.publishEvent(new UserSessionStateChangeEvent(this, userId, UserState.MAIN_MENU));
            applicationEventPublisher.publishEvent(new EditMessageTextEvent(
                    this, messageService.backToMainMenuMessage(chatId, update.getMessage().getText(), editMessageId)));
        } else {
            log.info("Новый пользователь на регистрацию {}", userId);
            applicationEventPublisher.publishEvent(new SendMessageEvent(this, messageService.greetingNonRegisteredUser(chatId)));
            toRegister.add(userId);
        }
    }

    @Override
    public void consumeCallbackQuery(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage sendMessage = messageService.unknownCommandMessage( chatId);
        applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));

    }
}
