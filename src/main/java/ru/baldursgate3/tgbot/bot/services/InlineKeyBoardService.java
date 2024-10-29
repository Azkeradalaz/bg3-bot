package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@RequiredArgsConstructor
@Service
public class InlineKeyBoardService {
    private final ButtonService buttonService;

    public InlineKeyboardMarkup getGreetingInlineKeyboard(){
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(new InlineKeyboardRow(
                                buttonService.standardButton("Создать нового персонажа","createNewGameCharacter")))
                .keyboardRow(new InlineKeyboardRow(
                                buttonService.standardButton("Показать сохраненных персонажей","getGameCharacterList"))).build();
    }

    public InlineKeyboardMarkup getCharStatsKeyboard(String name, Integer str, Integer dex, Integer con, Integer intellect, Integer wis, Integer cha ){
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(new InlineKeyboardRow())
                .keyboardRow(
                        new InlineKeyboardRow(
                                buttonService.standardButton("Имя: "+name,"setCharName")))
                .keyboardRow(
                        new InlineKeyboardRow(
                                buttonService.standardButton("СИЛ: " + str.toString(),"setStr")))
                .keyboardRow(
                        new InlineKeyboardRow(
                                buttonService.standardButton("ЛОВ: "+dex.toString(),"setDex")))
                .keyboardRow(
                        new InlineKeyboardRow(
                                buttonService.standardButton("ВЫН: "+con.toString(),"setCon")))
                .keyboardRow(
                        new InlineKeyboardRow(
                                buttonService.standardButton("ИНТ: "+intellect.toString(),"setInt")))
                .keyboardRow(
                        new InlineKeyboardRow(
                                buttonService.standardButton("МУД: "+wis.toString(),"setWis")))
                .keyboardRow(
                        new InlineKeyboardRow(
                                buttonService.standardButton("ХАР: "+cha.toString() ,"setCha")))
                .build();

    }
}
