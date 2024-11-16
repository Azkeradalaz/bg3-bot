package ru.baldursgate3.tgbot.bot.states;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
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
@Component
@RequiredArgsConstructor
public class MainMenuState implements UserSessionState {
    private final UserService userService;
    private final MessageService messageService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CurrentGameCharacterEditService currentGameCharacterEditService;

    @Override
    public void consumeMessage(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        if (messageService.getEditMessage(chatId) == null) {
            SendMessage sendMessage = messageService.greetingRegisteredUser(userId, userService.getUserName(userId));
            applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
        } else {
            SendMessage sendMessage = messageService.unknownCommandMessage(chatId);
            applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
        }
    }

    @Override
    public void consumeCallbackQuery(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String callData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long messageId = update.getCallbackQuery().getMessage().getMessageId().longValue();
        EditMessageText editMessageText = null;
        UserState userState = null;

        if (callData.equals("createNewGameCharacter")) {
            currentGameCharacterEditService.put(userId, GameCharacterEditor.getDefault(userService.getUserDto(userId)));
            editMessageText = messageService.characterEdit(chatId, messageId, currentGameCharacterEditService.get(userId));
            userState = UserState.CHARACTER_EDIT;

        } else if (callData.equals("getGameCharacterList")) {
            editMessageText = messageService.getCharacterList(chatId, messageId, userId);
            userState = UserState.CHARACTER_LIST;
        } else {
            SendMessage sendMessage = messageService.unknownCommandMessage(chatId);
            applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
        }

        if (editMessageText != null) {
            applicationEventPublisher.publishEvent(new EditMessageTextEvent(this, editMessageText));
        }
        if (userState != null) {
            applicationEventPublisher.publishEvent(new UserSessionStateChangeEvent(this, userId, userState));
        }
    }
}
