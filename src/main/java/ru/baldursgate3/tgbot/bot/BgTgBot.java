package ru.baldursgate3.tgbot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.baldursgate3.tgbot.bot.config.Bg3Config;
import ru.baldursgate3.tgbot.bot.event.EditMessageTextEvent;
import ru.baldursgate3.tgbot.bot.event.SendMessageEvent;
import ru.baldursgate3.tgbot.bot.services.ConsumeUpdateService;
import ru.baldursgate3.tgbot.bot.services.MessageService;
import ru.baldursgate3.tgbot.bot.services.UserSessionStateService;

@Slf4j
@Component
public class BgTgBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final ConsumeUpdateService consumeUpdateService;
    private final MessageService messageService;
    private final UserSessionStateService userSessionStateService;
    private final Bg3Config bg3ConfigProperties;

    public BgTgBot(ConsumeUpdateService consumeUpdateService, MessageService messageService, UserSessionStateService userSessionStateService, Bg3Config bg3ConfigProperties) {
        this.consumeUpdateService = consumeUpdateService;
        this.messageService = messageService;
        this.userSessionStateService = userSessionStateService;
        this.bg3ConfigProperties = bg3ConfigProperties;
        telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @Override
    public String getBotToken() {
        return bg3ConfigProperties.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {

        if (update.hasMessage()) {
            Long userId = update.getMessage().getFrom().getId();
            Long chatId = update.getMessage().getChatId();
            userSessionStateService.getSessionState(userId, chatId).consumeMessage(update);
        } else if (update.hasCallbackQuery()) {
            Long userId = update.getCallbackQuery().getFrom().getId();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            userSessionStateService.getSessionState(userId, chatId).consumeCallbackQuery(update);
        }


//        MessageDto messageDto = consumeUpdateService.consumeUpdate(update);
//        SendMessage sendMessage = messageDto.sendMessage();
//        EditMessageText editMessageText = messageDto.editMessageText();
//        List<DeleteMessage> deleteMessage = messageDto.deleteMessage();
//        if (sendMessage != null) {
//            try {
//                Message tmp = telegramClient.execute(sendMessage);
//                messageService.putDeleteMessage(tmp.getChatId(), tmp.getMessageId().longValue());
//            } catch (TelegramApiException e) {
//                log.error("Ошибка отправки сообщения {}", e);
//            }
//        }
//        if (editMessageText != null) {
//            try {
//                telegramClient.execute(editMessageText);
//            } catch (TelegramApiException e) {
//                log.error("Ошибка изменения сообщения {}", e);
//            }
//        }
//        if (deleteMessage != null) {
//            for (DeleteMessage delete : deleteMessage) {
//                try {
//                    telegramClient.execute(delete);
//                } catch (TelegramApiException e) {
//                    log.error("Ошибка удаления сообщения {}", e);
//                }
//            }
//        }
    }

    @EventListener
    public void sendMessageEventHandler(SendMessageEvent sendMessageEvent) {
        SendMessage sendMessage = sendMessageEvent.getSendMessage();
        try {
            Message tmp = telegramClient.execute(sendMessage);
            messageService.putDeleteMessage(tmp.getChatId(), tmp.getMessageId().longValue());
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения {}", e);
        }
    }

    @EventListener
    public void editMessageTextEventHandler(EditMessageTextEvent editMessageTextEvent) {
        EditMessageText editMessageText = editMessageTextEvent.getEditMessageText();
        try {
            telegramClient.execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error("Ошибка изменения сообщения {}", e);
        }
    }
}
