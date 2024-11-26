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

import java.util.Map;
import java.util.function.BiConsumer;


@Slf4j
@Component
@RequiredArgsConstructor
public class MainMenuState implements SessionState {
    private final UserService userService;
    private final MessageService messageService;
    private final SessionService sessionService;
    private final GameCharacterService gameCharacterService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private static final String CREATE_NEW_GAME_CHARACTER = "createNewGameCharacter";
    private static final String GET_GAME_CHARACTER_LIST = "getGameCharacterList";
    private final Map<String, BiConsumer> commands = Map.of(
            CREATE_NEW_GAME_CHARACTER, createNewGameCharacter(),
            GET_GAME_CHARACTER_LIST, getGameCharacterList()
    );

    @Override
    public void consumeMessage(Long userId, Long chatId, String message) {
        SendMessage sendMessage = messageService.unknownCommandMessage(chatId);
        applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
    }

    @Override
    public void consumeCallbackQuery(Long userId, Long chatId, String callData) {
        if (commands.containsKey(callData)) {
            commands.get(callData).accept(userId, chatId);
        } else {
            applicationEventPublisher.publishEvent(new SendMessageEvent(
                    this, messageService.unknownCommandMessage(chatId)));
        }
    }

    @Override
    public void sendDefaultMessage(Long userId, Long chatId) {
        SendMessage sendMessage = messageService.greetingRegisteredUser(userId, userService.getUserName(userId));
        applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
    }

    private BiConsumer<Long, Long> createNewGameCharacter() {
        return (userId, chatId) -> {
            Long editMessage = messageService.getEditMessage(chatId);
            GameCharacterDto newGameCharacterDto = gameCharacterService.getDefault(userService.getUserDto(userId));
            Long gameCharacterId = gameCharacterService.save(newGameCharacterDto);
            sessionService.setGameCharacterId(userId, gameCharacterId);
            sessionService.setSessionState(userId, UserState.CHARACTER_EDIT);
            EditMessageText editMessageText = messageService.characterEdit(chatId, editMessage, newGameCharacterDto);
            applicationEventPublisher.publishEvent(new EditMessageTextEvent(this, editMessageText));
        };
    }

    private BiConsumer<Long, Long> getGameCharacterList() {
        return (userId, chatId) -> {
            Long editMessage = messageService.getEditMessage(chatId);
            sessionService.setSessionState(userId, UserState.CHARACTER_LIST);
            EditMessageText editMessageText = messageService.getCharacterList(chatId, editMessage, userId);
            applicationEventPublisher.publishEvent(new EditMessageTextEvent(this, editMessageText));
        };
    }
}
