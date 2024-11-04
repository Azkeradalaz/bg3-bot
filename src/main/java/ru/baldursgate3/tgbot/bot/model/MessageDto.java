package ru.baldursgate3.tgbot.bot.model;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.List;

public record MessageDto(SendMessage sendMessage,
                         EditMessageText editMessageText,
                         List<DeleteMessage> deleteMessage) {
}
