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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.baldursgate3.tgbot.bot.conts.Constant;
import ru.baldursgate3.tgbot.bot.entities.GameCharacter;
import ru.baldursgate3.tgbot.bot.services.RestTemplateService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Component
public class BgTgBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final RestTemplateService restTemplateService;

    private final Set<Long> toRegister = new HashSet<>();
    private final Map<Long, String> activeUser = new HashMap<>();
    private final Map<Long, GameCharacter> activeGameCharacter = new HashMap<>();

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
            Long chatId = update.getMessage().getChatId();

            User user = update.getMessage().getFrom();
            String responseMessage = restTemplateService.getUserByTgId(user.getId());

            SendMessage message = null;

            if (responseMessage != null) {
                activeUser.put(user.getId(), responseMessage);
                message = SendMessage.builder().chatId(chatId).text("Добрый день, " + responseMessage +
                                "! Доступные команды.")
                        .replyMarkup(InlineKeyboardMarkup
                                .builder()
                                .keyboardRow(new InlineKeyboardRow(
                                                InlineKeyboardButton
                                                        .builder()
                                                        .text("Создать нового персонажа")
                                                        .callbackData("createNewGameCharacter")
                                                        .build()
                                        )
                                )
                                .keyboardRow(new InlineKeyboardRow(
                                                InlineKeyboardButton
                                                        .builder()
                                                        .text("Показать сохраненных персонажей")
                                                        .callbackData("getGameCharacterList")
                                                        .build()
                                        )
                                )
                                .build()
                        )
                        .build();
            } else if (!activeUser.containsKey(user.getId())) {
                if (toRegister.contains(user.getId())) {
                    String tmpMsg = restTemplateService.registerUser(user.getId(), messageText);
                    toRegister.remove(user.getId());
                    message = SendMessage.builder().chatId(chatId).text("Добрый день, " + tmpMsg +
                            "! Доступные команды.").build();

                } else if (responseMessage == null) {
                    message = SendMessage.builder().chatId(chatId).text("Представьтесь, пожалуйста.").build();
                    toRegister.add(user.getId());
                }
            }

            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        } else if (update.hasCallbackQuery()) {
            String callData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            long userId = update.getCallbackQuery().getMessage().getChat().getId();
            EditMessageText newMessage = null;
            String answer = "";
            SendMessage message = null;

            if (callData.equals("createNewGameCharacter")) {
                answer = "Выберите имя и характеристики персонажа";
                message = SendMessage.builder()
                        .chatId(chatId)
                        .text(answer)
                        .replyMarkup(InlineKeyboardMarkup
                                .builder()
                                .keyboardRow(new InlineKeyboardRow())
                                .keyboardRow(
                                        new InlineKeyboardRow(
                                                InlineKeyboardButton
                                                        .builder()
                                                        .text("Имя")
                                                        .callbackData("setCharName")
                                                        .build(),
                                                InlineKeyboardButton
                                                        .builder()
                                                        .text("Тав")
                                                        .callbackData("setCharName")
                                                        .build()
                                        )
                                )
                                .keyboardRow(
                                        new InlineKeyboardRow(
                                                InlineKeyboardButton
                                                        .builder()
                                                        .text("СИЛ")
                                                        .callbackData("setStr")
                                                        .build(),
                                                InlineKeyboardButton
                                                        .builder()
                                                        .text("10")
                                                        .callbackData("setStr")
                                                        .build()
                                        )
                                )
                                .keyboardRow(
                                        new InlineKeyboardRow(
                                                InlineKeyboardButton
                                                        .builder()
                                                        .text("ЛОВ")
                                                        .callbackData("setDex")
                                                        .build(),
                                                InlineKeyboardButton
                                                        .builder()
                                                        .text("10")
                                                        .callbackData("setDex")
                                                        .build()
                                        )
                                )
                                .keyboardRow(
                                        new InlineKeyboardRow(
                                                InlineKeyboardButton
                                                        .builder()
                                                        .text("ВЫН")
                                                        .callbackData("setCon")
                                                        .build(),
                                                InlineKeyboardButton
                                                        .builder()
                                                        .text("10")
                                                        .callbackData("setCon")
                                                        .build()
                                        )
                                )
                                .keyboardRow(
                                        new InlineKeyboardRow(
                                                InlineKeyboardButton
                                                        .builder()
                                                        .text("ИНТ")
                                                        .callbackData("setInt")
                                                        .build(),
                                                InlineKeyboardButton
                                                        .builder()
                                                        .text("10")
                                                        .callbackData("setInt")
                                                        .build()
                                        )
                                )
                                .keyboardRow(
                                        new InlineKeyboardRow(
                                                InlineKeyboardButton
                                                        .builder()
                                                        .text("МУД")
                                                        .callbackData("setWis")
                                                        .build(),
                                                InlineKeyboardButton
                                                        .builder()
                                                        .text("10")
                                                        .callbackData("setWis")
                                                        .build()
                                        )
                                )
                                .keyboardRow(
                                        new InlineKeyboardRow(
                                                InlineKeyboardButton
                                                        .builder()
                                                        .text("ХАР")
                                                        .callbackData("setCha")
                                                        .build(),
                                                InlineKeyboardButton
                                                        .builder()
                                                        .text("10")
                                                        .callbackData("setCha")
                                                        .build()
                                        )
                                )

                                .build()
                        )
                        .build();

            } else if (callData.equals("setCharName")) {
                answer = "Введите имя персонажа:";
                message = SendMessage.builder()
                        .chatId(chatId)
                        .text(answer)
                        .build();

            } else if (callData.equals("setStr")) {
                answer = "Введите покатель силы:";
                message = SendMessage.builder()
                        .chatId(chatId)
                        .text(answer)
                        .build();

            } else if (callData.equals("setDex")) {
                answer = "Введите покатель ловкости:";
                message = SendMessage.builder()
                        .chatId(chatId)
                        .text(answer)
                        .build();

            } else if (callData.equals("setCon")) {
                answer = "Введите покатель выносливости:";
                message = SendMessage.builder()
                        .chatId(chatId)
                        .text(answer)
                        .build();

            } else if (callData.equals("setInt")) {
                answer = "Введите покатель интеллекта:";
                message = SendMessage.builder()
                        .chatId(chatId)
                        .text(answer)
                        .build();

            } else if (callData.equals("setWis")) {
                answer = "Введите покатель мудрости:";
                message = SendMessage.builder()
                        .chatId(chatId)
                        .text(answer)
                        .build();

            } else if (callData.equals("setCha")) {
                answer = "Введите покатель харизмы:";
                message = SendMessage.builder()
                        .chatId(chatId)
                        .text(answer)
                        .build();

            }else if (callData.equals("getGameCharacterList")) {
                answer = "Получаем список персонажей";
                message = SendMessage.builder()
                        .chatId(chatId)
                        .text(answer)
                        .build();

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
