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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class BgTgBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final RestTemplateService restTemplateService;

    private Set<Long> toRegister = new HashSet<>();
    private Map<Long, String> activeUser= new HashMap<>();

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
            String responseMessage = restTemplateService.getUserByTgId(user.getId());

            SendMessage message = null;


            if(!activeUser.containsKey(user.getId())){
                if (toRegister.contains(user.getId())) {
                    String tmpMsg = restTemplateService.registerUser(user.getId(),messageText);
                    toRegister.remove(user.getId());
                    message = SendMessage.builder().chatId(chatId).text("Внимание, "+ tmpMsg+"! Этот бот нихера не может. Спасибо за внимание.").build();

                } else if (responseMessage==null) {
                    message = SendMessage.builder().chatId(chatId).text("Представьтесь, пожалуйста.").build();
                    toRegister.add(user.getId());
                } else {
                    message = SendMessage.builder().chatId(chatId).text("Ошибка.").build();;
                }

            }else {










            }




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
