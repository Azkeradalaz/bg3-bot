package ru.baldursgate3.tgbot.bot.model;

public record SessionDto(Long userTgId,
                         Long gameCharacterId,
                         String userState) {

}
