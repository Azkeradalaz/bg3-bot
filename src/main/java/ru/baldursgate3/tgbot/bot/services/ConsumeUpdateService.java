package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
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
    private final Map<Long, Long> currentMessageCharEdit = new HashMap<>();


    public void consumeUpdate(Update update, TelegramClient telegramClient) {
        EditMessageText newMessage = null;
        SendMessage message = null;
        DeleteMessage deleteMessage = null;

        if (update.hasMessage() && update.getMessage().hasText()) {

            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            User user = update.getMessage().getFrom();
            long userId = user.getId();

            ru.baldursgate3.tgbot.bot.entities.User responseUser = restTemplateService.getUserByTgId(user.getId());

            if (!(userStateMap.get(userId) == UserState.DEFAULT || userStateMap.get(userId) == null)) {
                CharacterEditor.setValues(activeGameCharacter.get(userId), userStateMap.get(userId), messageText);
                userStateMap.put(userId, UserState.DEFAULT);
                newMessage = messageService.characterEdit(
                        chatId,
                        currentMessageCharEdit.get(userId),
                        activeGameCharacter.get(userId));

                System.out.println(activeGameCharacter.get(userId));

            } else if (responseUser != null) {
                activeUser.put(user.getId(), responseUser.getName());
                userStateMap.put(user.getId(), UserState.DEFAULT);
                message = messageService.greetingRegisteredUser(chatId, responseUser.getName());


            } else if (!activeUser.containsKey(user.getId())) {

                if (toRegister.contains(user.getId())) {
                    String registerUser = restTemplateService.registerUser(user.getId(), messageText);
                    toRegister.remove(user.getId());
                    userStateMap.put(user.getId(), UserState.DEFAULT);
                    message = messageService.greetingRegisteredUser(chatId, registerUser);

                } else if (responseUser == null) {
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
                GameCharacter edit = activeGameCharacter.get(update.getCallbackQuery().getFrom().getId());
                edit.setUser(restTemplateService.getUserByTgId(userId));

                newMessage = messageService.characterEdit(chatId, messageId, edit);
                currentMessageCharEdit.put(userId,messageId);
                System.out.println(activeGameCharacter);
            } else if (callData.equals("setCharName")) {
                message = messageService.statChangeMessage(chatId, "Введите имя персонажа:");
                userStateMap.put(userId, UserState.CHANGING_CHARACTER_NAME);
            } else if (callData.equals("setStr")) {
                message = messageService.statChangeMessage(chatId, "Введите показатель силы:");
                userStateMap.put(userId, UserState.CHANGING_CHARACTER_STR);
            } else if (callData.equals("setDex")) {
                message = messageService.statChangeMessage(chatId, "Введите показатель ловкости:");
                userStateMap.put(userId, UserState.CHANGING_CHARACTER_DEX);
            } else if (callData.equals("setCon")) {
                message = messageService.statChangeMessage(chatId, "Введите показатель выносливости:");
                userStateMap.put(userId, UserState.CHANGING_CHARACTER_CON);
            } else if (callData.equals("setInt")) {
                message = messageService.statChangeMessage(chatId, "Введите показатель интеллекта:");
                userStateMap.put(userId, UserState.CHANGING_CHARACTER_INT);
            } else if (callData.equals("setWis")) {
                message = messageService.statChangeMessage(chatId, "Введите показатель мудрости:");
                userStateMap.put(userId, UserState.CHANGING_CHARACTER_WIS);
            } else if (callData.equals("setCha")) {
                message = messageService.statChangeMessage(chatId, "Введите показатель харизмы:");
                userStateMap.put(userId, UserState.CHANGING_CHARACTER_CHA);
            }else if (callData.equals("saveCharacter")) {
                message = messageService.statChangeMessage(chatId, "Персонаж "+activeGameCharacter.get(userId).getName()+ " сохранён");
                deleteMessage = messageService.deleteMessage(chatId,messageId);

                restTemplateService.saveGameCharacter(activeGameCharacter.get(userId));
                currentMessageCharEdit.remove(userId);

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
        }
        if (newMessage != null) {
            try {
                telegramClient.execute(newMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        if(deleteMessage!=null){
            try {
                telegramClient.execute(deleteMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


    }

}
