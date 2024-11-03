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

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ConsumeUpdateService {

    private final RestTemplateService restTemplateService;
    private final MessageService messageService;
    private final UserService userService;
    private final Map<Long, GameCharacterDto> activeGameCharacter = new HashMap<>();
    private final Map<Long, UserState> userStateMap = new HashMap<>();
    private final Map<Long, Long> currentMessageCharEdit = new HashMap<>();


    public MessageDto consumeUpdate(Update update) {
        SendMessage message = null;
        EditMessageText editMessage = null;
        DeleteMessage deleteMessage = null;

        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            long userId = update.getMessage().getFrom().getId();

            if (userService.isRegistered(userId)) {
                if (!(userStateMap.get(userId) == UserState.DEFAULT || userStateMap.get(userId) == null)) {
                    activeGameCharacter.put(userId, CharacterEditor.setValues(activeGameCharacter.get(userId), userStateMap.get(userId), messageText));
                    userStateMap.put(userId, UserState.DEFAULT);
                    editMessage = messageService.characterEdit(
                            chatId,
                            currentMessageCharEdit.get(userId),
                            activeGameCharacter.get(userId));
                } else {
                    String userName = userService.getUserName(userId);
                    userStateMap.put(userId, UserState.DEFAULT);
                    message = messageService.greetingRegisteredUser(chatId, userName);
                }
            } else {
                message = userService.processNonRegisteredUser(chatId, userId, messageText);
            }


        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String callData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long userId = update.getCallbackQuery().getFrom().getId();

            if (callData.equals("createNewGameCharacter")) {
                activeGameCharacter.put(userId, new GameCharacterDto(null, "Тав",
                        userService.getUserDto(userId), (short) 10, (short) 10, (short) 10, (short) 10, (short) 10, (short) 10));
                GameCharacterDto edit = activeGameCharacter.get(update.getCallbackQuery().getFrom().getId());

                editMessage = messageService.characterEdit(chatId, messageId, edit);
                currentMessageCharEdit.put(userId, messageId);
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
            } else if (callData.equals("saveCharacter")) {
                message = messageService.statChangeMessage(chatId, "Персонаж " + activeGameCharacter.get(userId).name() + " сохранён");
                deleteMessage = messageService.deleteMessage(chatId, messageId);

                restTemplateService.saveGameCharacter(activeGameCharacter.get(userId));
                currentMessageCharEdit.remove(userId);

            } else if (callData.equals("getGameCharacterList")) {
                editMessage = messageService.getCharacterList(chatId, messageId, userId);
            } else if (callData.matches("delete[\\d]+")) {
                restTemplateService.deleteCharacter(Long.parseLong(callData.replace("delete","")));
                editMessage = messageService.getCharacterList(chatId, messageId, userId);

            } else if (callData.matches("edit[\\d]+")) {


                GameCharacterDto edit = activeGameCharacter.get(update.getCallbackQuery().getFrom().getId());
                editMessage = messageService.characterEdit(chatId, messageId, edit);
                currentMessageCharEdit.put(userId, messageId);
            } else if (callData.equals("backToMainMenu")) {
                String userName = userService.getUserName(userId);
                editMessage = messageService.backToMainMenuMessage(chatId,userName,messageId);
            }
        }

        return new MessageDto(message, editMessage, deleteMessage);

    }

}
