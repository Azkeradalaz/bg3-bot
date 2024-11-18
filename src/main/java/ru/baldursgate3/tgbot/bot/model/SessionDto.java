package ru.baldursgate3.tgbot.bot.model;

import ru.baldursgate3.tgbot.bot.states.UserSessionState;

import java.util.Date;

public record SessionDto (Long sessionId, Long userId, Long chatId, UserSessionState userSessionState, Date dateCreated, Date dateExpire){
}
