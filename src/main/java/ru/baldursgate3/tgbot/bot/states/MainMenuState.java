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
import ru.baldursgate3.tgbot.bot.services.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class MainMenuState implements SessionState {
    private final UserService userService;
    private final MessageService messageService;
    private final SessionService sessionService;
    private final GameCharacterService gameCharacterService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void consumeMessage(Long userId, Long chatId, String message) {
        SendMessage sendMessage;
        if (messageService.getEditMessage(chatId) == null) {
            sendMessage = messageService.greetingRegisteredUser(userId, userService.getUserName(userId));

        } else {
            sendMessage = messageService.unknownCommandMessage(chatId);
        }
        applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
    }

    @Override
    public void consumeCallbackQuery(Long userId, Long chatId, String callData) {
        Long editMessage = messageService.getEditMessage(chatId);
        EditMessageText editMessageText = null;

        if (callData.equals("createNewGameCharacter")) {
            GameCharacterDto newGameCharacterDto = gameCharacterService.getDefault(userService.getUserDto(userId));
            Long gameCharacterId = gameCharacterService.save(newGameCharacterDto);
            sessionService.setGameCharacterId(userId, gameCharacterId);
            sessionService.setSessionState(userId,UserState.CHARACTER_EDIT);
            editMessageText = messageService.characterEdit(chatId, editMessage, newGameCharacterDto);

        } else if (callData.equals("getGameCharacterList")) {
            sessionService.setSessionState(userId,UserState.CHARACTER_EDIT);
            editMessageText = messageService.getCharacterList(chatId, editMessage, userId);

        } else {
            applicationEventPublisher.publishEvent(new SendMessageEvent(
                    this, messageService.unknownCommandMessage(chatId)));
        }

        applicationEventPublisher.publishEvent(new EditMessageTextEvent(this, editMessageText));
    }
}
