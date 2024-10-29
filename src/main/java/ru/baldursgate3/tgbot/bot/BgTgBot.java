package ru.baldursgate3.tgbot.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.baldursgate3.tgbot.bot.entities.GameCharacter;
import ru.baldursgate3.tgbot.bot.services.MessageService;
import ru.baldursgate3.tgbot.bot.services.RestTemplateService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Component
public class BgTgBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final RestTemplateService restTemplateService;

    private final MessageService messageService;

    private final Set<Long> toRegister = new HashSet<>();
    private final Map<Long, String> activeUser = new HashMap<>();
    private final Map<Long, GameCharacter> activeGameCharacter = new HashMap<>();

    public BgTgBot(RestTemplateService restTemplateService, MessageService messageService) {
        this.restTemplateService = restTemplateService;
        this.messageService = messageService;
        telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @Override
    public String getBotToken() { //TODO
        return System.getenv("TOKEN");
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {

        EditMessageText newMessage = null;
        SendMessage message = null;

        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();

            String messageText = update.getMessage().getText();
            User user = update.getMessage().getFrom();
            String responseUserName = restTemplateService.getUserByTgId(user.getId());

            if (responseUserName != null) {
                activeUser.put(user.getId(), responseUserName);
                message = messageService.greetingRegisteredUser(chatId, responseUserName);

            } else if (!activeUser.containsKey(user.getId())) {

                if (toRegister.contains(user.getId())) {
                    String registerUser = restTemplateService.registerUser(user.getId(), messageText);
                    toRegister.remove(user.getId());
                    message = messageService.greetingRegisteredUser(chatId, registerUser);

                } else if (responseUserName == null) {
                    message = messageService.greetingNonRegisteredUser(chatId);
                    toRegister.add(user.getId());
                }
            }

        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String callData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();

            if (callData.equals("createNewGameCharacter")) {
                newMessage = messageService.newCharacter(chatId,messageId);
                activeGameCharacter.put(update.getCallbackQuery().getFrom().getId(),new GameCharacter());
                System.out.println(activeGameCharacter);
            } else if (callData.equals("setCharName")) {
                message = messageService.statChangeMessage(chatId, "Введите имя персонажа:");
            } else if (callData.equals("setStr")) {
                message = messageService.statChangeMessage(chatId, "Введите покатель силы:");
            } else if (callData.equals("setDex")) {
                message = messageService.statChangeMessage(chatId, "Введите покатель ловкости:");
            } else if (callData.equals("setCon")) {
                message = messageService.statChangeMessage(chatId, "Введите покатель выносливости:");
            } else if (callData.equals("setInt")) {
                message = messageService.statChangeMessage(chatId, "Введите покатель интеллекта:");
            } else if (callData.equals("setWis")) {
                message = messageService.statChangeMessage(chatId, "Введите покатель мудрости:");
            } else if (callData.equals("setCha")) {
                message = messageService.statChangeMessage(chatId, "Введите покатель харизмы:");
            } else if (callData.equals("getGameCharacterList")) {//todo
                newMessage = messageService.getCharacterList(chatId, messageId);
            }
        }
        if (message != null) {
            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            try {
                telegramClient.execute(newMessage);
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
