package ru.baldursgate3.tgbot.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.baldursgate3.tgbot.bot.model.MessageDto;
import ru.baldursgate3.tgbot.bot.services.ConsumeUpdateService;
import ru.baldursgate3.tgbot.bot.services.MessageService;
import ru.baldursgate3.tgbot.bot.services.SessionStateService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BgTgBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final ConsumeUpdateService consumeUpdateService;
    private final MessageService messageService;
    private final SessionStateService sessionStateService;

    @Override
    public String getBotToken() {
        return null;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChat().getId();
        sessionStateService.getSession(userId, chatId).getUserSessionState().consumeUpdate();

        MessageDto messageDto = consumeUpdateService.consumeUpdate(update);
        SendMessage sendMessage = messageDto.sendMessage();
        EditMessageText editMessageText = messageDto.editMessageText();
        List<DeleteMessage> deleteMessage = messageDto.deleteMessage();
        if (sendMessage != null) {
            try {
                Message tmp = telegramClient.execute(sendMessage);
                messageService.putDeleteMessage(tmp.getChatId(), tmp.getMessageId().longValue());
            } catch (TelegramApiException e) {
                e.printStackTrace();//todo
            }
        }
        if (editMessageText != null) {
            try {
                telegramClient.execute(editMessageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();//todo
            }
        }
        if (deleteMessage != null) {
            try {
                for (DeleteMessage delete : deleteMessage) {
                    telegramClient.execute(delete);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();//todo
            }
        }
    }
}
