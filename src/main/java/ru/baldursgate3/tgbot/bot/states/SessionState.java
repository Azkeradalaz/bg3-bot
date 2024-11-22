package ru.baldursgate3.tgbot.bot.states;

public interface SessionState {
    void consumeMessage(Long userId, Long chatId, String message);
    void consumeCallbackQuery(Long userId, Long chatId, String callData);
    void sendDefaultMessage(Long userId, Long chatId);
}
