package ru.baldursgate3.tgbot.bot.model;

public record GameCharacterDto(String name,
                               UserDto userDto,
                               short strength,
                               short dexterity,
                               short constitution,
                               short intellect,
                               short wisdom,
                               short charisma) {
}
