package ru.baldursgate3.tgbot.bot.model;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public record MessageDto(SendMessage sendMessage, EditMessageText editMessageText, DeleteMessage deleteMessage) {
}
