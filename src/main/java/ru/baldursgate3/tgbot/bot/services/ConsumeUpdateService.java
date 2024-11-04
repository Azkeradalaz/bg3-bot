package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.baldursgate3.tgbot.bot.CharacterEditor;
import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.model.GameCharacterDto;
import ru.baldursgate3.tgbot.bot.model.MessageDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ConsumeUpdateService {

    private final RestTemplateService restTemplateService;
    private final MessageService messageService;
    private final UserStateService userStateService;
    private final UserService userService;
    private final Map<Long, GameCharacterDto> activeGameCharacter = new HashMap<>();
    private final Map<Long, Long> currentMessageCharEdit = new HashMap<>();




    public MessageDto consumeUpdate(Update update) {
        SendMessage message = null;
        EditMessageText editMessage = null;
        List<DeleteMessage> deleteMessages = null;


        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            long userId = update.getMessage().getFrom().getId();
            long messageId = update.getMessage().getMessageId();

            if (userService.isRegistered(userId)) {
                messageService.putDeleteMessage(chatId,messageId);
                if (!(userStateService.isOfState(userId, UserState.DEFAULT)|| userStateService.isOfState(userId,null))) {
                    activeGameCharacter.put(userId, CharacterEditor.setValues(
                            activeGameCharacter.get(userId),
                            userStateService.get(userId),
                            messageText));
                    userStateService.set(userId, UserState.DEFAULT);
                    editMessage = messageService.characterEdit(
                            chatId,
                            currentMessageCharEdit.get(userId),
                            activeGameCharacter.get(userId));
                } else {
                    String userName = userService.getUserName(userId);
                    userStateService.set(userId, UserState.DEFAULT);
                    message = messageService.greetingRegisteredUser(chatId, userName);
                }
            } else {
                message = userService.processNonRegisteredUser(chatId, userId, messageText);
                messageService.putDeleteMessage(chatId,messageId);
            }


        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String callData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long userId = update.getCallbackQuery().getFrom().getId();
            String userName = userService.getUserName(userId);

            if (callData.equals("createNewGameCharacter")) {
                activeGameCharacter.put(userId, new GameCharacterDto(null, "Тав",
                        userService.getUserDto(userId), (short) 10, (short) 10, (short) 10, (short) 10, (short) 10, (short) 10));
                GameCharacterDto edit = activeGameCharacter.get(update.getCallbackQuery().getFrom().getId());
                editMessage = messageService.characterEdit(chatId, messageId, edit);
                currentMessageCharEdit.put(userId, messageId);
            } else if (callData.equals("setCharName")) {
                message = messageService.statChangeMessage(chatId, "Введите имя персонажа:");
                userStateService.set(userId, UserState.CHANGING_CHARACTER_NAME);
            } else if (callData.equals("setStr")) {
                message = messageService.statChangeMessage(chatId, "Введите показатель силы:");
                userStateService.set(userId, UserState.CHANGING_CHARACTER_STR);
            } else if (callData.equals("setDex")) {
                message = messageService.statChangeMessage(chatId, "Введите показатель ловкости:");
                userStateService.set(userId, UserState.CHANGING_CHARACTER_DEX);
            } else if (callData.equals("setCon")) {
                message = messageService.statChangeMessage(chatId, "Введите показатель выносливости:");
                userStateService.set(userId, UserState.CHANGING_CHARACTER_CON);
            } else if (callData.equals("setInt")) {
                message = messageService.statChangeMessage(chatId, "Введите показатель интеллекта:");
                userStateService.set(userId, UserState.CHANGING_CHARACTER_INT);
            } else if (callData.equals("setWis")) {
                message = messageService.statChangeMessage(chatId, "Введите показатель мудрости:");
                userStateService.set(userId, UserState.CHANGING_CHARACTER_WIS);
            } else if (callData.equals("setCha")) {
                message = messageService.statChangeMessage(chatId, "Введите показатель харизмы:");
                userStateService.set(userId, UserState.CHANGING_CHARACTER_CHA);
            } else if (callData.equals("saveCharacter")) {
                message = messageService.statChangeMessage(chatId, "Персонаж " + activeGameCharacter.get(userId).name() + " сохранён");
                editMessage = messageService.backToMainMenuMessage(chatId,userName,messageId);
                restTemplateService.saveGameCharacter(activeGameCharacter.get(userId));
                userStateService.set(userId,UserState.DEFAULT);
                currentMessageCharEdit.remove(userId);
                deleteMessages = messageService.getDeleteMessages(chatId);

            } else if (callData.equals("getGameCharacterList")) {
                editMessage = messageService.getCharacterList(chatId, messageId, userId);
            } else if (callData.matches("delete[\\d]+")) {
                restTemplateService.deleteCharacter(Long.parseLong(callData.replace("delete","")));
                editMessage = messageService.getCharacterList(chatId, messageId, userId);

            } else if (callData.matches("edit[\\d]+")) {
                GameCharacterDto edit = restTemplateService.getGameCharacter(Long.parseLong(callData.replace("edit","")));
                activeGameCharacter.put(userId,edit);
                editMessage = messageService.characterEdit(chatId, messageId, edit);
                currentMessageCharEdit.put(userId, messageId);
            } else if (callData.equals("backToMainMenu")) {
                editMessage = messageService.backToMainMenuMessage(chatId,userName,messageId);
            }
        }

        return new MessageDto(message, editMessage, deleteMessages);

    }

}
