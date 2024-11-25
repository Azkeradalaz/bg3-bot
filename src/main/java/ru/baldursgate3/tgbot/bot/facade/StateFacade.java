package ru.baldursgate3.tgbot.bot.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.baldursgate3.tgbot.bot.event.DeleteMessagesEvent;
import ru.baldursgate3.tgbot.bot.services.MessageService;
import ru.baldursgate3.tgbot.bot.services.StateHandlerService;

@Service
@RequiredArgsConstructor
public class StateFacade {
    private final MessageService messageService;
    private final StateHandlerService stateHandlerService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void consumeMessage(Long userId, Long chatId, Long messageId, String message) {
        messageService.putDeleteMessage(chatId, messageId);

        if (messageService.editMessageNotPresent(chatId)) {
            stateHandlerService.getSessionState(userId).sendDefaultMessage(userId, chatId);

        } else {
            stateHandlerService.getSessionState(userId).consumeMessage(userId, chatId, message);
        }
    }

    public void consumeCallbackQuery(Long userId, Long chatId, Long messageId, String callData) {

        if (messageService.editMessageNotPresent(chatId)) {
            messageService.putDeleteMessage(chatId, messageId);
            stateHandlerService.getSessionState(userId).sendDefaultMessage(userId, chatId);

        } else if(messageService.getEditMessage(chatId).equals(messageId)){
            stateHandlerService.getSessionState(userId).consumeCallbackQuery(userId, chatId, callData);

        } else {
            messageService.putDeleteMessage(chatId, messageId);
            applicationEventPublisher
                    .publishEvent(new DeleteMessagesEvent(this, messageService.getDeleteMessages(chatId)));
        }
    }
}