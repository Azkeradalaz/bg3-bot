package ru.baldursgate3.tgbot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.baldursgate3.tgbot.bot.config.Bg3Config;
import ru.baldursgate3.tgbot.bot.event.DeleteMessagesEvent;
import ru.baldursgate3.tgbot.bot.event.EditMessageTextEvent;
import ru.baldursgate3.tgbot.bot.event.SendMessageEvent;
import ru.baldursgate3.tgbot.bot.facade.StateFacade;
import ru.baldursgate3.tgbot.bot.services.MessageService;

@Slf4j
@Component
public class BgTgBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final MessageService messageService;
    private final Bg3Config bg3ConfigProperties;
    private final StateFacade stateFacade;

    /*иначе не создаётся telegramClient.*/
    public BgTgBot(MessageService messageService, Bg3Config bg3ConfigProperties, StateFacade stateFacade) {
        this.messageService = messageService;
        this.bg3ConfigProperties = bg3ConfigProperties;
        this.stateFacade = stateFacade;
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
            Message msg = update.getMessage();
            stateFacade.consumeMessage(
                    msg.getFrom().getId(),
                    msg.getChatId(),
                    msg.getMessageId().longValue(),
                    msg.getText());

        } else if (update.hasCallbackQuery()) {
            CallbackQuery cbq = update.getCallbackQuery();
            stateFacade.consumeCallbackQuery(
                    cbq.getFrom().getId(),
                    cbq.getMessage().getChatId(),
                    cbq.getMessage().getMessageId().longValue(),
                    cbq.getData());
        }
    }

    @EventListener
    public void sendMessageEventHandler(SendMessageEvent sendMessageEvent) {
        SendMessage sendMessage = sendMessageEvent.getSendMessage();
        try {
            Message tmp = telegramClient.execute(sendMessage);
            if (messageService.getEditMessage(tmp.getChatId()) == null) {
                messageService.putEditMessage(tmp.getChatId(), tmp.getMessageId().longValue());
            } else if (messageService.getEditMessage(tmp.getChatId()) != tmp.getMessageId().longValue()) {
                messageService.putDeleteMessage(tmp.getChatId(), tmp.getMessageId().longValue());
            }
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения {}", e);
        }
    }

    @EventListener
    public ApplicationEvent editMessageTextEventHandler(EditMessageTextEvent editMessageTextEvent) {
        EditMessageText editMessageText = editMessageTextEvent.getEditMessageText();
        try {
            telegramClient.execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error("Ошибка изменения сообщения {}", e);
        }
        return new DeleteMessagesEvent(this, messageService.getDeleteMessages(Long.valueOf(editMessageTextEvent.getEditMessageText().getChatId())));
    }

    @EventListener
    public void deleteMessageEventHandler(DeleteMessagesEvent deleteMessage) {
        DeleteMessages deleteMessages = deleteMessage.getDeleteMessages();
        if (deleteMessages != null) {
            try {
                telegramClient.execute(deleteMessages);
            } catch (TelegramApiException e) {
                log.error("Ошибка удаления сообщения {}", e);
            }
        }
    }
}
