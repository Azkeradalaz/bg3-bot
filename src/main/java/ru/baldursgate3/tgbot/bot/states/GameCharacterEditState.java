package ru.baldursgate3.tgbot.bot.states;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.event.EditMessageTextEvent;
import ru.baldursgate3.tgbot.bot.event.SendMessageEvent;
import ru.baldursgate3.tgbot.bot.model.GameCharacterDto;
import ru.baldursgate3.tgbot.bot.services.GameCharacterService;
import ru.baldursgate3.tgbot.bot.services.MessageService;
import ru.baldursgate3.tgbot.bot.services.SessionService;
import ru.baldursgate3.tgbot.bot.services.UserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameCharacterEditState implements SessionState {
    private final UserService userService;
    private final MessageService messageService;
    private final SessionService sessionService;
    private final GameCharacterService gameCharacterService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Map<Long, String> previousCallback = new HashMap<>();

    @Override
    public void consumeMessage(Long userId, Long chatId, String message) {

        if (previousCallback.get(userId) != null) {
            Long gameCharacterId = sessionService.getGameCharacterId(userId);
            GameCharacterDto edit = gameCharacterService.getGameCharacter(gameCharacterId);

            if ((!previousCallback.get(userId).equals("setCharName")
                    && message.chars().allMatch(Character::isDigit))
                    || previousCallback.get(userId).equals("setCharName")) {
                edit = gameCharacterService.setValues(edit, previousCallback.get(userId), message);

                gameCharacterService.save(edit);
                previousCallback.remove(userId);

                EditMessageText editMessageText = messageService.characterEdit(
                        chatId, messageService.getEditMessage(chatId), edit);
                applicationEventPublisher.publishEvent(new EditMessageTextEvent(this, editMessageText));

            } else {
                SendMessage sendMessage = messageService.messageMustBeNumeric(chatId);
                applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
            }

        } else {
            SendMessage sendMessage = messageService.unknownCommandMessage(chatId);
            applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
        }
    }

    @Override
    public void consumeCallbackQuery(Long userId, Long chatId, String callData) {
        String userName = userService.getUserName(userId);
        Long editMessage = messageService.getEditMessage(chatId);
        SendMessage sendMessage = null;
        EditMessageText editMessageText = null;

        if (previousCallback.get(userId) != null) {
            previousCallback.remove(userId);
        }

        if (callData.matches("set[a-zA-Z]+")) {
            previousCallback.put(userId, callData);
            log.info("прошлый колбэк {}", previousCallback.get(userId));
            sendMessage = messageService.statChangeMessage(chatId, callData);

        } else if (callData.equals("saveCharacter")) {
            sessionService.setGameCharacterId(userId, null);
            sessionService.setSessionState(userId, UserState.MAIN_MENU);
            editMessageText = messageService.backToMainMenuMessage(chatId, userName, editMessage);

        } else if (callData.equals("backToMainMenu")) {
            Long gameCharacterId = sessionService.getGameCharacterId(userId);
            sessionService.setGameCharacterId(userId, null);
            sessionService.setSessionState(userId, UserState.MAIN_MENU);
            gameCharacterService.delete(gameCharacterId);
            editMessageText = messageService.backToMainMenuMessage(chatId, userName, editMessage);

        } else if (callData.equals("backToCharacterList")) {
            sessionService.setGameCharacterId(userId, null);
            sessionService.setSessionState(userId, UserState.CHARACTER_LIST);
            editMessageText = messageService.getCharacterList(chatId, editMessage, userId);

        } else {
            sendMessage = messageService.unknownCommandMessage(chatId);
        }

        if (sendMessage != null) {
            applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
        }
        if (editMessageText != null) {
            applicationEventPublisher.publishEvent(new EditMessageTextEvent(this, editMessageText));
        }
    }

    @Override
    public void sendDefaultMessage(Long userId, Long chatId) {
        SendMessage sendMessage = messageService.characterEdit(chatId, gameCharacterService.getGameCharacter(
                                sessionService.getGameCharacterId(userId)));
        applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
    }
}
