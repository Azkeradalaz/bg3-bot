package ru.baldursgate3.tgbot.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.baldursgate3.tgbot.bot.config.Bg3ConfigProperties;
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
        consumeUpdateService.consumeUpdate(update, telegramClient);

    }
}
