package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.baldursgate3.tgbot.bot.CharacterEditor;
import ru.baldursgate3.tgbot.bot.UserState;
import ru.baldursgate3.tgbot.bot.entities.GameCharacter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ConsumeUpdateService {

    private final RestTemplateService restTemplateService;
    private final MessageService messageService;
    private final Set<Long> toRegister = new HashSet<>();
    private final Map<Long, String> activeUser = new HashMap<>();
    private final Map<Long, GameCharacter> activeGameCharacter = new HashMap<>();
    private final Map<Long, UserState> userStateMap = new HashMap<>();


    public void consumeUpdate(Update update, TelegramClient telegramClient) {
        EditMessageText newMessage = null;
        SendMessage message = null;

        if (update.hasMessage() && update.getMessage().hasText()) {

            Long chatId = update.getMessage().getChatId();

            String messageText = update.getMessage().getText();
            User user = update.getMessage().getFrom();
            long userId = user.getId();
            String responseUserName = restTemplateService.getUserByTgId(user.getId());

            if (!(userStateMap.get(userId) == UserState.DEFAULT || userStateMap.get(userId) == null)) {
                CharacterEditor.setValues(activeGameCharacter.get(userId), userStateMap.get(userId), messageText);
                userStateMap.put(userId, UserState.DEFAULT);
                System.out.println(activeGameCharacter.get(userId));
            } else if (responseUserName != null) {
                activeUser.put(user.getId(), responseUserName);
                userStateMap.put(user.getId(), UserState.DEFAULT);
                message = messageService.greetingRegisteredUser(chatId, responseUserName);


            } else if (!activeUser.containsKey(user.getId())) {

                if (toRegister.contains(user.getId())) {
                    String registerUser = restTemplateService.registerUser(user.getId(), messageText);
                    toRegister.remove(user.getId());
                    userStateMap.put(user.getId(), UserState.DEFAULT);
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
            long userId = update.getCallbackQuery().getFrom().getId();

            if (callData.equals("createNewGameCharacter")) {
                activeGameCharacter.put(userId, new GameCharacter());
                newMessage = messageService.characterEdit(chatId, messageId, activeGameCharacter.get(update.getCallbackQuery().getFrom().getId()));
                System.out.println(activeGameCharacter);
            } else if (callData.equals("setCharName")) {
                message = messageService.statChangeMessage(chatId, "Введите имя персонажа:");
                userStateMap.put(userId, UserState.CHANGING_CHARACTER_NAME);
            } else if (callData.equals("setStr")) {
                message = messageService.statChangeMessage(chatId, "Введите покатель силы:");
                userStateMap.put(userId, UserState.CHANGING_CHARACTER_STR);
            } else if (callData.equals("setDex")) {
                message = messageService.statChangeMessage(chatId, "Введите покатель ловкости:");
                userStateMap.put(userId, UserState.CHANGING_CHARACTER_DEX);
            } else if (callData.equals("setCon")) {
                message = messageService.statChangeMessage(chatId, "Введите покатель выносливости:");
                userStateMap.put(userId, UserState.CHANGING_CHARACTER_CON);
            } else if (callData.equals("setInt")) {
                message = messageService.statChangeMessage(chatId, "Введите покатель интеллекта:");
                userStateMap.put(userId, UserState.CHANGING_CHARACTER_INT);
            } else if (callData.equals("setWis")) {
                message = messageService.statChangeMessage(chatId, "Введите покатель мудрости:");
                userStateMap.put(userId, UserState.CHANGING_CHARACTER_WIS);
            } else if (callData.equals("setCha")) {
                message = messageService.statChangeMessage(chatId, "Введите покатель харизмы:");
                userStateMap.put(userId, UserState.CHANGING_CHARACTER_CHA);
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
        } else if (newMessage != null) {
            try {
                telegramClient.execute(newMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


    }

}
