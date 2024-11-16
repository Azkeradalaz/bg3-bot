package ru.baldursgate3.tgbot.bot.states;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.event.EditMessageTextEvent;
import ru.baldursgate3.tgbot.bot.event.SendMessageEvent;
import ru.baldursgate3.tgbot.bot.event.UserSessionStateChangeEvent;
import ru.baldursgate3.tgbot.bot.model.GameCharacterDto;
import ru.baldursgate3.tgbot.bot.services.CurrentGameCharacterEditService;
import ru.baldursgate3.tgbot.bot.services.MessageService;
import ru.baldursgate3.tgbot.bot.services.RestTemplateService;
import ru.baldursgate3.tgbot.bot.services.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameCharacterListState implements UserSessionState {

    private final UserService userService;
    private final MessageService messageService;
    private final RestTemplateService restTemplateService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CurrentGameCharacterEditService currentGameCharacterEditService;

    @Override
    public void consumeMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = messageService.unknownCommandMessage(chatId);
        applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
    }

    @Override
    public void consumeCallbackQuery(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String callData = update.getCallbackQuery().getData();
        Long editMessage = messageService.getEditMessage(chatId);
        Long userId = update.getCallbackQuery().getFrom().getId();
        String userName = userService.getUserName(userId);
        UserState userState = null;
        EditMessageText editMessageText = null;

        if (callData.matches("delete[\\d]+")) {
            Long charId = Long.parseLong(callData.replace("delete", ""));
            currentGameCharacterEditService.put(userId, restTemplateService.getGameCharacter(charId));
            userState = UserState.CHARACTER_DELETE;
            editMessageText = messageService.deleteCharacterConfirm(chatId, editMessage, currentGameCharacterEditService.get(userId).name());

        } else if (callData.matches("edit[\\d]+")) {
            GameCharacterDto edit = restTemplateService.getGameCharacter(Long.parseLong(callData.replace("edit", "")));
            currentGameCharacterEditService.put(userId, edit);
            userState = UserState.CHARACTER_EDIT;
            editMessageText = messageService.characterEdit(chatId, editMessage, edit);

        } else if (callData.equals("backToMainMenu")) {
            userState = UserState.MAIN_MENU;
            editMessageText = messageService.backToMainMenuMessage(chatId, userName, editMessage);
        } else {
            SendMessage sendMessage = messageService.unknownCommandMessage(chatId);
            applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
        }

        if (userState != null) {
            applicationEventPublisher.publishEvent(new UserSessionStateChangeEvent(this, userId, userState));
        }
        if (editMessageText != null) {
            applicationEventPublisher.publishEvent(new EditMessageTextEvent(this, editMessageText));
        }
    }
}
