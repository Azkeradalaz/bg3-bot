package ru.baldursgate3.tgbot.bot.states;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.event.DeleteMessagesEvent;
import ru.baldursgate3.tgbot.bot.event.EditMessageTextEvent;
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
    }

    @Override
    public void consumeCallbackQuery(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String callData = update.getCallbackQuery().getData();
        Long messageId = messageService.getEditMessage(chatId);
        Long userId = update.getCallbackQuery().getFrom().getId();
        String userName = userService.getUserName(userId);

        if (callData.matches("delete[\\d]+")) {
            restTemplateService.deleteCharacter(Long.parseLong(callData.replace("delete", "")));
            applicationEventPublisher.publishEvent(new EditMessageTextEvent(this, messageService.getCharacterList(chatId, messageId, userId)));
        } else if (callData.matches("edit[\\d]+")) {
            GameCharacterDto edit = restTemplateService.getGameCharacter(Long.parseLong(callData.replace("edit", "")));
            currentGameCharacterEditService.put(userId, edit);
            applicationEventPublisher.publishEvent(new EditMessageTextEvent(this, messageService.characterEdit(chatId, messageId, edit)));
            applicationEventPublisher.publishEvent(new UserSessionStateChangeEvent(this, userId, UserState.CHARACTER_EDIT));
        } else if (callData.equals("backToMainMenu")) {
            applicationEventPublisher.publishEvent(new EditMessageTextEvent(this,messageService.backToMainMenuMessage(chatId, userName, messageId)));
            applicationEventPublisher.publishEvent(new UserSessionStateChangeEvent(this, userId, UserState.MAIN_MENU));
            if (messageService.getDeleteMessages(chatId) != null) {
                applicationEventPublisher.publishEvent(new DeleteMessagesEvent(this, messageService.getDeleteMessages(chatId)));
            }
        }
    }
}
