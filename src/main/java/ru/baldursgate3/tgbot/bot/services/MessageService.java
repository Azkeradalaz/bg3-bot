package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.baldursgate3.tgbot.bot.entities.GameCharacter;
import ru.baldursgate3.tgbot.bot.model.GameCharacterDto;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final InlineKeyBoardService inlineKeyBoardService;

    public SendMessage greetingNonRegisteredUser(Long chatId) {
        return SendMessage.builder().chatId(chatId).text("Представьтесь, пожалуйста.").build();
    }

    public SendMessage greetingRegisteredUser(Long chatId, String responseUserMessage) {
        return SendMessage.builder().chatId(chatId).text("Добрый день, " + responseUserMessage +
                        "! Доступные команды.")
                .replyMarkup(inlineKeyBoardService.getGreetingInlineKeyboard()).build();
    }

    public SendMessage statChangeMessage(Long chatId, String message) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .build();
    }
    public DeleteMessage deleteMessage(Long chatId, Long messageId){
        return DeleteMessage.builder().chatId(chatId).messageId(Math.toIntExact(messageId)).build();
    }

    public EditMessageText characterEdit(Long chatId, long messageId, GameCharacterDto gameCharacter){
        return EditMessageText.builder()
                .chatId(chatId)
                .messageId((int) messageId)
                .text("Выберите имя и характеристики персонажа")
                .replyMarkup(inlineKeyBoardService.getCharStatsKeyboard(
                        gameCharacter.name(),
                        gameCharacter.strength(),
                        gameCharacter.dexterity(),
                        gameCharacter.constitution(),
                        gameCharacter.intellect(),
                        gameCharacter.wisdom(),
                        gameCharacter.charisma())
                )
                .build();
    }

    public EditMessageText getCharacterList(Long chatId, long messageId, Long userId){
        return EditMessageText.builder()
                .chatId(chatId)
                .messageId((int) messageId)
                .text("Ваши персонажи")
                .replyMarkup(inlineKeyBoardService.getListOfSavedGameCharacter(userId))//todo
                .build();
    }








    //todo лист персов


}
