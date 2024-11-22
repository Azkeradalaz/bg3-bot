package ru.baldursgate3.tgbot.bot.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.baldursgate3.tgbot.bot.services.MessageService;
import ru.baldursgate3.tgbot.bot.services.StateHandlerService;

@Service
@RequiredArgsConstructor
public class StateFacade {
    private final StateHandlerService stateHandlerService;
    private final MessageService messageService;

    public void consumeMessage(Long userId, Long chatId, Long messageId, String message){
        messageService.putDeleteMessage(chatId, messageId);
        stateHandlerService.getSessionState(userId)
                .consumeMessage(userId, chatId, message);
    }

    public void consumeCallbackQuery(Long userId, Long chatId, String callData) {
        stateHandlerService.getSessionState(userId)
                .consumeCallbackQuery(userId, chatId, callData);
    }
}