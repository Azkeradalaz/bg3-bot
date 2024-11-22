package ru.baldursgate3.tgbot.bot.states;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.event.EditMessageTextEvent;
import ru.baldursgate3.tgbot.bot.event.SendMessageEvent;
import ru.baldursgate3.tgbot.bot.services.GameCharacterService;
import ru.baldursgate3.tgbot.bot.services.MessageService;
import ru.baldursgate3.tgbot.bot.services.SessionService;

@Component
@RequiredArgsConstructor
public class GameCharacterDeleteState implements SessionState {
    private final SessionService sessionService;
    private final MessageService messageService;
    private final GameCharacterService gameCharacterService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void consumeMessage(Long userId, Long chatId, String message) {
        SendMessage sendMessage = messageService.unknownCommandMessage(chatId);
        applicationEventPublisher
                .publishEvent(new SendMessageEvent(this, sendMessage));
    }

    @Override
    public void consumeCallbackQuery(Long userId, Long chatId, String callData) {
        Long editMessage = messageService.getEditMessage(chatId);
        EditMessageText editMessageText = null;

        if (callData.equals("backToCharacterList")) {
            sessionService
                    .setSessionState(userId, UserState.CHARACTER_LIST);
            editMessageText = messageService.getCharacterList(chatId, editMessage, userId);

        } else if (callData.equals("deleteCharacter")) {
            Long gameCharacterId = sessionService.getGameCharacterId(userId);
            sessionService
                    .setSessionState(userId, UserState.CHARACTER_LIST);
            sessionService
                    .setGameCharacterId(userId, null);
            gameCharacterService
                    .delete(gameCharacterId);
            editMessageText = messageService.getCharacterList(chatId, editMessage, userId);

        } else {
            SendMessage sendMessage = messageService.unknownCommandMessage(chatId);
            applicationEventPublisher
                    .publishEvent(new SendMessageEvent(this, sendMessage));
        }

        if (editMessageText != null) {
            applicationEventPublisher
                    .publishEvent(new EditMessageTextEvent(this, editMessageText));
        }

    }

    @Override
    public void sendDefaultMessage(Long userId, Long chatId) {
        String gameCharacterName = gameCharacterService.getGameCharacterName(
                sessionService.getGameCharacterId(userId));
        SendMessage sendMessage = messageService.deleteCharacterConfirm(chatId, gameCharacterName);
        applicationEventPublisher
                .publishEvent(new SendMessageEvent(this, sendMessage));
    }
}
