package ru.baldursgate3.tgbot.bot.states;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserSessionState {
    void consumeMessage(Update update);
    void consumeCallbackQuery(Update update);
}
