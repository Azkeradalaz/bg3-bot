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
import ru.baldursgate3.tgbot.bot.services.RestTemplateService;
import ru.baldursgate3.tgbot.bot.services.UserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameCharacterEditState implements UserSessionState {
    private final UserService userService;
    private final MessageService messageService;
    private final RestTemplateService restTemplateService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CurrentGameCharacterEditService currentGameCharacterEditService;
    private final Map<Long, String> previousCallBack = new HashMap<>();

    @Override
    public void consumeMessage(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        if (previousCallBack.get(userId) != null) {
            EditMessageText editMessageText = null;
            currentGameCharacterEditService.put(
                    userId,
                    GameCharacterEditor.setValues(
                            currentGameCharacterEditService.get(userId),
                            previousCallBack.get(userId),
                            update.getMessage().getText()));
            editMessageText = messageService.characterEdit(chatId, messageService.getEditMessage(chatId), currentGameCharacterEditService.get(userId));
            previousCallBack.remove(userId);
            applicationEventPublisher.publishEvent(new EditMessageTextEvent(this, editMessageText));
        }
        else {
            SendMessage sendMessage = messageService.unknownCommandMessage( chatId);
            applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
        }

    }

    @Override
    public void consumeCallbackQuery(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String callData = update.getCallbackQuery().getData();
        Long messageId = update.getCallbackQuery().getMessage().getMessageId().longValue();
        Long userId = update.getCallbackQuery().getFrom().getId();
        String userName = userService.getUserName(userId);//переделать на сессию?
        UserState userState = null;
        SendMessage sendMessage = null;
        EditMessageText editMessageText = null;

        if (previousCallBack.get(userId) != null) {
            previousCallBack.remove(userId);
        }

        if (callData.matches("set[a-zA-Z]+")) {
            if (previousCallBack.get(userId) == null) {
                sendMessage = messageService.statChangeMessage(chatId, callData);
                previousCallBack.put(userId, callData);
            }
        } else if (callData.equals("saveCharacter")) {
            restTemplateService.saveGameCharacter(currentGameCharacterEditService.get(userId));
            currentGameCharacterEditService.remove(userId);
            editMessageText = messageService.backToMainMenuMessage(chatId, userName, messageId);
            userState = UserState.MAIN_MENU;

        } else if (callData.equals("backToMainMenu")) {
            editMessageText = messageService.backToMainMenuMessage(chatId, userName, messageId);
            userState = UserState.MAIN_MENU;

        } else if (callData.equals("backToCharacterList")) {
            editMessageText = messageService.getCharacterList(chatId, messageId, userId);
            userState = UserState.CHARACTER_LIST;
        } else {
            sendMessage = messageService.unknownCommandMessage(chatId);
        }

        if (userState != null) {
            applicationEventPublisher.publishEvent(new UserSessionStateChangeEvent(this, userId, userState));
        }
        if (sendMessage != null) {
            applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
        }
        if (editMessageText != null) {
            applicationEventPublisher.publishEvent(new EditMessageTextEvent(this, editMessageText));
        }
    }
}
