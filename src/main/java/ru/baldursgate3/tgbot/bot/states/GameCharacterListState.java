package ru.baldursgate3.tgbot.bot.states;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.event.EditMessageTextEvent;
import ru.baldursgate3.tgbot.bot.event.SendMessageEvent;
import ru.baldursgate3.tgbot.bot.model.GameCharacterDto;
import ru.baldursgate3.tgbot.bot.services.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameCharacterListState implements SessionState {

    private final UserService userService;
    private final MessageService messageService;
    private final SessionService sessionService;
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
        String userName = userService.getUserName(userId);
        EditMessageText editMessageText = null;

        if (callData.matches("delete[\\d]+")) {
            Long gameCharacterId = Long.parseLong(callData.replace("delete", ""));
            String gameCharacterName = gameCharacterService.getGameCharacterName(gameCharacterId);

            sessionService
                    .setGameCharacterId(userId, gameCharacterId);
            sessionService
                    .setSessionState(userId, UserState.CHARACTER_DELETE);
            editMessageText = messageService.deleteCharacterConfirm(chatId, editMessage, gameCharacterName);

        } else if (callData.matches("edit[\\d]+")) {
            Long gameCharacterId = Long.parseLong(callData.replace("edit", ""));
            GameCharacterDto edit = gameCharacterService.getGameCharacter(gameCharacterId);
            sessionService
                    .setGameCharacterId(userId, edit.id());
            sessionService
                    .setSessionState(userId, UserState.CHARACTER_EDIT);
            log.info("{} редактирует персонажа {}", userName, edit);
            editMessageText = messageService.characterEdit(chatId, editMessage, edit);

        } else if (callData.equals("backToMainMenu")) {
            sessionService
                    .setSessionState(userId, UserState.MAIN_MENU);
            editMessageText = messageService.backToMainMenuMessage(chatId, userName, editMessage);

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
        SendMessage sendMessage = messageService.getCharacterList(chatId, userId);
        applicationEventPublisher
                .publishEvent(new SendMessageEvent(this, sendMessage));
    }
}
