package ru.baldursgate3.tgbot.bot;

import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.model.GameCharacterDto;
import ru.baldursgate3.tgbot.bot.model.UserDto;

public class GameCharacterEditor {
    public static GameCharacterDto setValues(GameCharacterDto gameCharacter, String previousCallBack, String message) {
        Long id = gameCharacter.id();
        String name = gameCharacter.name();
        UserDto userDto = gameCharacter.userDto();
        short strength = gameCharacter.strength();
        short dexterity = gameCharacter.dexterity();
        short constitution = gameCharacter.constitution();
        short intellect = gameCharacter.intellect();
        short wisdom = gameCharacter.wisdom();
        short charisma = gameCharacter.charisma();

        switch (previousCallBack) {
            case "setCharName":
                if (message.length() > 30) {
                    name = message.substring(0, 30);
                } else {
                    name = message;
                }
                break;
            case "setStr":
                strength = Short.parseShort(message);
                break;
            case "setDex":
                dexterity = Short.parseShort(message);
                break;
            case "setCon":
                constitution = Short.parseShort(message);
                break;
            case "setInt":
                intellect = Short.parseShort(message);
                break;
            case "setWis":
                wisdom = Short.parseShort(message);
                break;
            case "setCha":
                charisma = Short.parseShort(message);
                break;
            default:
                break;
        }
        return new GameCharacterDto(id, name, userDto, strength, dexterity, constitution, intellect, wisdom, charisma);
    }

    public static GameCharacterDto getDefault(UserDto userDto) {
        return new GameCharacterDto(null, "Тав", userDto, (short) 10, (short) 10, (short) 10, (short) 10, (short) 10, (short) 10);
    }
}
