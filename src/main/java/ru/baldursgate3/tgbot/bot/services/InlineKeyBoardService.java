package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@RequiredArgsConstructor
@Service
public class InlineKeyBoardService {
    private final ButtonService buttonService;

    public InlineKeyboardMarkup getGreetingInlineKeyboard() {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(new InlineKeyboardRow(
                        buttonService.standardButton("Создать нового персонажа", "createNewGameCharacter")))
                .keyboardRow(new InlineKeyboardRow(
                        buttonService.standardButton("Показать сохраненных персонажей", "getGameCharacterList"))).build();
    }

    public InlineKeyboardMarkup getCharStatsKeyboard(String name, Short str, Short dex, Short con, Short intellect, Short wis, Short cha) {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(new InlineKeyboardRow())
                .keyboardRow(
                        new InlineKeyboardRow(
                                buttonService.standardButton("Имя: " + name, "setCharName")))
                .keyboardRow(
                        new InlineKeyboardRow(
                                buttonService.standardButton("СИЛ: " + str, "setStr")))
                .keyboardRow(
                        new InlineKeyboardRow(
                                buttonService.standardButton("ЛОВ: " + dex, "setDex")))
                .keyboardRow(
                        new InlineKeyboardRow(
                                buttonService.standardButton("ВЫН: " + con, "setCon")))
                .keyboardRow(
                        new InlineKeyboardRow(
                                buttonService.standardButton("ИНТ: " + intellect, "setInt")))
                .keyboardRow(
                        new InlineKeyboardRow(
                                buttonService.standardButton("МУД: " + wis, "setWis")))
                .keyboardRow(
                        new InlineKeyboardRow(
                                buttonService.standardButton("ХАР: " + cha, "setCha")))
                .build();

    }
}
