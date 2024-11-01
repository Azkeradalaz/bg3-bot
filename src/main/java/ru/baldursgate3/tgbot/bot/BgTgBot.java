package ru.baldursgate3.tgbot.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.baldursgate3.tgbot.bot.config.Bg3ConfigProperties;
import ru.baldursgate3.tgbot.bot.model.MessageDto;
import ru.baldursgate3.tgbot.bot.services.ConsumeUpdateService;


@Component
public class BgTgBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final ConsumeUpdateService consumeUpdateService;

    private final Bg3ConfigProperties bg3ConfigProperties;

    public BgTgBot(ConsumeUpdateService consumeUpdateService, Bg3ConfigProperties bg3ConfigProperties) {
        this.consumeUpdateService = consumeUpdateService;
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
        MessageDto messageDto = consumeUpdateService.consumeUpdate(update);
        SendMessage sendMessage = messageDto.sendMessage();
        EditMessageText editMessageText = messageDto.editMessageText();
        DeleteMessage deleteMessage = messageDto.deleteMessage();

        if (sendMessage != null) {
            try {
                telegramClient.execute(sendMessage);
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
                telegramClient.execute(deleteMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();//todo
            }
        }


    }
}
