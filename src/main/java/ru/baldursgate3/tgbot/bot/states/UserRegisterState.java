package ru.baldursgate3.tgbot.bot.states;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.baldursgate3.tgbot.bot.event.EditMessageTextEvent;
import ru.baldursgate3.tgbot.bot.event.SendMessageEvent;
import ru.baldursgate3.tgbot.bot.services.MessageService;
import ru.baldursgate3.tgbot.bot.services.UserService;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegisterState implements UserSessionState{
    private final UserService userService;
    private final MessageService messageService;
    private final ApplicationEventPublisher applicationEventPublisher;

    private Set<Long> toRegister = new HashSet<>();

    @Override
    public void consumeMessage(Update update) {

        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        if (toRegister.contains(userId)){
            toRegister.remove(userId);
            userService.registerUser(userId,update.getMessage().getText());
            applicationEventPublisher.publishEvent(new EditMessageTextEvent(this, messageService.backToMainMenuMessage(chatId,update.getMessage().getText(), messageService.getEditMessage(chatId))));
        }
        else {
            applicationEventPublisher.publishEvent(new SendMessageEvent(this,messageService.greetingNonRegisteredUser(chatId)));
            toRegister.add(userId);
        }
    }

    @Override
    public void consumeCallbackQuery(Update update) {

    }
}
