package ru.baldursgate3.tgbot.bot.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public InlineKeyboardMarkup getListOfSavedGameCharacter(Long userId) {
        ObjectMapper mapper = new ObjectMapper();
        List<GameCharacterDto> list = mapper.convertValue(
                restTemplateService.getListOfGameCharacters(userId),
                new TypeReference<List<GameCharacterDto>>() {
                });
        List<InlineKeyboardRow> listOfRows = new ArrayList<>();
        for (GameCharacterDto gameChar : list) {
            listOfRows.add(new InlineKeyboardRow(
                            buttonService.standardButton(
                                    gameChar.name(),
                                    "edit" + gameChar.id()),
                            buttonService.standardButton("\uD83D\uDDD1",
                                    "delete" + gameChar.id())
                    )
            );
        }
        listOfRows.add(new InlineKeyboardRow(buttonService.standardButton("Назад", "backToMainMenu")));
        return new InlineKeyboardMarkup(listOfRows);
    }

    public InlineKeyboardMarkup getCharStatsKeyboard(String name, Short str, Short dex, Short con, Short intellect, Short wis, Short cha, String callBack) {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(new InlineKeyboardRow(buttonService.standardButton("Имя: " + name, "setCharName")))
                .keyboardRow(new InlineKeyboardRow(buttonService.standardButton("СИЛ: " + str, "setStr")))
                .keyboardRow(new InlineKeyboardRow(buttonService.standardButton("ЛОВ: " + dex, "setDex")))
                .keyboardRow(new InlineKeyboardRow(buttonService.standardButton("ВЫН: " + con, "setCon")))
                .keyboardRow(new InlineKeyboardRow(buttonService.standardButton("ИНТ: " + intellect, "setInt")))
                .keyboardRow(new InlineKeyboardRow(buttonService.standardButton("МУД: " + wis, "setWis")))
                .keyboardRow(new InlineKeyboardRow(buttonService.standardButton("ХАР: " + cha, "setCha")))
                .keyboardRow(new InlineKeyboardRow(buttonService.standardButton("Назад", callBack)))
                .keyboardRow(new InlineKeyboardRow(buttonService.standardButton("Сохранить", "saveCharacter")))
                .build();
    }

    public InlineKeyboardMarkup getDeleteGameCharacterKeyboard() {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(new InlineKeyboardRow(buttonService.standardButton("Назад", "backToCharacterList")))
                .keyboardRow(new InlineKeyboardRow(buttonService.standardButton("Подтвердить", "deleteCharacter")))
                .build();
    }
}
