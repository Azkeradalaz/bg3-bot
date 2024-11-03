package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.baldursgate3.tgbot.bot.model.GameCharacterDto;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class InlineKeyBoardService {
    private final ButtonService buttonService;
    private final RestTemplateService restTemplateService;

    public InlineKeyboardMarkup getGreetingInlineKeyboard() {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(new InlineKeyboardRow(
                        buttonService.standardButton("Создать нового персонажа", "createNewGameCharacter")))
                .keyboardRow(new InlineKeyboardRow(
                        buttonService.standardButton("Показать сохраненных персонажей", "getGameCharacterList"))).build();
    }
    public InlineKeyboardMarkup getListOfSavedGameCharacter(Long userId){
        List<GameCharacterDto> list = restTemplateService.getListOfGameCharacters(userId);
        List<InlineKeyboardRow> listOfRows = new ArrayList<>();
        for (GameCharacterDto gameChar: list) {
            listOfRows.add(new InlineKeyboardRow(buttonService.standardButton(gameChar.name(),"edit"+gameChar.name()))); //todo тут ошибка каста class java.util.LinkedHashMap cannot be cast to class ru.baldursgate3.tgbot.bot.model.GameCharacterDto (java.util.LinkedHashMap is in module java.base of loader 'bootstrap'; ru.baldursgate3.tgbot.bot.model.GameCharacterDto is in unnamed module of loader 'app')
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(listOfRows);
        return inlineKeyboardMarkup;
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
                .keyboardRow(
                        new InlineKeyboardRow(
                                buttonService.standardButton("Сохранить", "saveCharacter")))
                .build();

    }
}
