package ru.baldursgate3.tgbot.bot.states;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.event.EditMessageTextEvent;
import ru.baldursgate3.tgbot.bot.event.SendMessageEvent;
import ru.baldursgate3.tgbot.bot.event.UserSessionStateChangeEvent;
import ru.baldursgate3.tgbot.bot.services.CurrentGameCharacterEditService;
import ru.baldursgate3.tgbot.bot.services.MessageService;
import ru.baldursgate3.tgbot.bot.services.RestTemplateService;
import ru.baldursgate3.tgbot.bot.services.UserService;

@Component
@RequiredArgsConstructor
public class GameCharacterDeleteState implements UserSessionState {
    private final MessageService messageService;
    private final RestTemplateService restTemplateService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CurrentGameCharacterEditService currentGameCharacterEditService;

    @Override
    public void consumeMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = messageService.unknownCommandMessage( chatId);
        applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
    }

    @Override
    public void consumeCallbackQuery(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String callData = update.getCallbackQuery().getData();
        Long editMessage = messageService.getEditMessage(chatId);
        Long userId = update.getCallbackQuery().getFrom().getId();
        UserState userState = null;
        EditMessageText editMessageText = null;

        if (callData.equals("backToCharacterList")) {
            currentGameCharacterEditService.remove(userId);
            userState = UserState.CHARACTER_LIST;
            editMessageText = messageService.getCharacterList(chatId, editMessage, userId);

        } else if (callData.equals("deleteCharacter")) {
            Long charId = currentGameCharacterEditService.get(userId).id();
            restTemplateService.deleteCharacter(charId);
            userState = UserState.CHARACTER_LIST;
            editMessageText = messageService.getCharacterList(chatId, editMessage, userId);
        } else {
            SendMessage sendMessage = messageService.unknownCommandMessage( chatId);
            applicationEventPublisher.publishEvent(new SendMessageEvent(this, sendMessage));
        }

        if(userState!=null){
        applicationEventPublisher.publishEvent(new UserSessionStateChangeEvent(this, userId, userState));
        }
        if (editMessageText!=null){
        applicationEventPublisher.publishEvent(new EditMessageTextEvent(this, editMessageText));
        }

    }
}
