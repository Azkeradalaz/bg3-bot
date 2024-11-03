package ru.baldursgate3.tgbot.bot;

import org.springframework.stereotype.Component;
import ru.baldursgate3.tgbot.bot.entities.GameCharacter;
import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.model.GameCharacterDto;
import ru.baldursgate3.tgbot.bot.model.UserDto;

@Component
public class CharacterEditor {
    public static GameCharacterDto setValues(GameCharacterDto gameCharacter, UserState state, String message) {
        Long id = gameCharacter.id();
        String name = gameCharacter.name();
        UserDto userDto=gameCharacter.userDto();
        short strength=gameCharacter.strength();
        short dexterity=gameCharacter.dexterity();
        short constitution= gameCharacter.constitution();
        short intellect= gameCharacter.intellect();
        short wisdom= gameCharacter.wisdom();
        short charisma= gameCharacter.charisma();

          switch (state) {
            case CHANGING_CHARACTER_NAME:
                if(message.length()>30){
                    name = message.substring(0,30);
                } else {
                    name = message;
                }
                break;
            case CHANGING_CHARACTER_STR:
                strength = Short.parseShort(message);
                break;
            case CHANGING_CHARACTER_DEX:
                dexterity =Short.parseShort(message);
                break;
            case CHANGING_CHARACTER_CON:
                constitution=Short.parseShort(message);
                break;
            case CHANGING_CHARACTER_INT:
                intellect = Short.parseShort(message);
                break;
            case CHANGING_CHARACTER_WIS:
                wisdom = Short.parseShort(message);
                break;
            case CHANGING_CHARACTER_CHA:
                charisma = Short.parseShort(message);
                break;
            default:
                break;
        }
        return new GameCharacterDto(id,name, userDto, strength,dexterity,constitution,intellect,wisdom,charisma);
    }
}
