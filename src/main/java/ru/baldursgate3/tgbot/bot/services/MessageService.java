package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.baldursgate3.tgbot.bot.entities.GameCharacter;

@RequiredArgsConstructor
@Service
public class MessageService {
    private final InlineKeyBoardService inlineKeyBoardService;

    public SendMessage greetingNonRegisteredUser(Long chatId) {
        return SendMessage.builder().chatId(chatId).text("Представьтесь, пожалуйста.").build();
    }

    public SendMessage greetingRegisteredUser(Long chatId, String responseUserName) {
        return SendMessage.builder().chatId(chatId).text("Добрый день, " + responseUserName +
                        "! Доступные команды.")
                .replyMarkup(inlineKeyBoardService.getGreetingInlineKeyboard()).build();
    }

    public SendMessage statChangeMessage(Long chatId, String message) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .build();
    }

    public EditMessageText characterEdit(Long chatId, long messageId, GameCharacter gameCharacter){
        return EditMessageText.builder()
                .chatId(chatId)
                .messageId((int) messageId)
                .text("Выберите имя и характеристики персонажа")
                .replyMarkup(inlineKeyBoardService.getCharStatsKeyboard(gameCharacter.getName(),
                        gameCharacter.getStrength(),
                        gameCharacter.getDexterity(),
                        gameCharacter.getConstitution(),
                        gameCharacter.getIntellect(),
                        gameCharacter.getWisdom(),
                        gameCharacter.getCharisma())
                )
                .build();
    }

    public EditMessageText getCharacterList(Long chatId, long messageId){
        return EditMessageText.builder()
                .chatId(chatId)
                .messageId((int) messageId)
                .text("Ваши персонажи")
                .replyMarkup(inlineKeyBoardService.getGreetingInlineKeyboard())//todo
                .build();
    }








    //todo лист персов


}
