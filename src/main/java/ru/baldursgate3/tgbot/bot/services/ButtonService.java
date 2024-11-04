package ru.baldursgate3.tgbot.bot.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Service
public class ButtonService {

    public InlineKeyboardButton standardButton(String text, String callBack){
        return InlineKeyboardButton
                .builder()
                .text(text)
                .callbackData(callBack)
                .build();

    }



}
