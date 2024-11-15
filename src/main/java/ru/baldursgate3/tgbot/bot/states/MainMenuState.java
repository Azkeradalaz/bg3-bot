package ru.baldursgate3.tgbot.bot.states;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.baldursgate3.tgbot.bot.GameCharacterEditor;
import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.event.EditMessageTextEvent;
import ru.baldursgate3.tgbot.bot.event.SendMessageEvent;
import ru.baldursgate3.tgbot.bot.event.UserSessionStateChangeEvent;
import ru.baldursgate3.tgbot.bot.services.CurrentGameCharacterEditService;
import ru.baldursgate3.tgbot.bot.services.MessageService;
import ru.baldursgate3.tgbot.bot.services.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainMenuState implements UserSessionState {
    private final UserService userService;
    private final MessageService messageService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CurrentGameCharacterEditService currentGameCharacterEditService;

    @Override
    public void consumeMessage(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        SendMessage sendMessage = messageService.greetingRegisteredUser(userId, userService.getUserName(userId));
        applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
    }

    @Override
    public void consumeCallbackQuery(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String callData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long messageId = update.getCallbackQuery().getMessage().getMessageId().longValue();
        EditMessageText editMessageText;

        if (callData.equals("createNewGameCharacter")) {
            currentGameCharacterEditService.put(userId, GameCharacterEditor.getDefault(userService.getUserDto(userId)));
            editMessageText = messageService.characterEdit(chatId, messageId, currentGameCharacterEditService.get(userId));
            applicationEventPublisher.publishEvent(new EditMessageTextEvent(this, editMessageText));
            applicationEventPublisher.publishEvent(new UserSessionStateChangeEvent(this, userId, UserState.CHARACTER_EDIT));


        } else if (callData.equals("getGameCharacterList")) {
            editMessageText = messageService.getCharacterList(chatId, messageId, userId);
            applicationEventPublisher.publishEvent(new EditMessageTextEvent(this, editMessageText));
            applicationEventPublisher.publishEvent(new UserSessionStateChangeEvent(this, userId, UserState.CHARACTER_LIST));
        }
    }
}
