package ru.baldursgate3.tgbot.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.baldursgate3.tgbot.bot.services.RestTemplateService;

@Component
public class BgTgBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final RestTemplateService restTemplateService;

    public BgTgBot(RestTemplateService restTemplateService) {
        this.restTemplateService = restTemplateService;
        telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @Override
    public String getBotToken() { //TODO
        return Constant.API_TOKEN;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            User user = update.getMessage().getFrom();
            String responceMessage = restTemplateService.getUser(user.getId());

            SendMessage message = null;

            if(!responceMessage.isBlank()){
                message = SendMessage.builder().chatId(chatId).text("Пользователь с ID:" + user.getId()).build();
            }

//            message = SendMessage
//                    .builder()
//                    .chatId(chatId)
//                    .text("User info: " + user)
//                    .build();

            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }
}
