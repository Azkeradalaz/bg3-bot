package ru.baldursgate3.tgbot.bot;

import org.springframework.stereotype.Component;
import ru.baldursgate3.tgbot.bot.entities.GameCharacter;

@Component
public class CharacterEditor {
    public static GameCharacter setValues(GameCharacter gameCharacter, UserState state, String message) {
          switch (state) {
            case CHANGING_CHARACTER_NAME:
                if(message.length()>30){
                    gameCharacter.setName(message.substring(0,30));
                } else {
                    gameCharacter.setName(message);
                }
                break;
            case CHANGING_CHARACTER_STR:
                gameCharacter.setStrength(Short.parseShort(message));
                break;
            case CHANGING_CHARACTER_DEX:
                gameCharacter.setDexterity(Short.parseShort(message));
                break;
            case CHANGING_CHARACTER_CON:
                gameCharacter.setConstitution(Short.parseShort(message));
                break;
            case CHANGING_CHARACTER_INT:
                gameCharacter.setIntellect(Short.parseShort(message));
                break;
            case CHANGING_CHARACTER_WIS:
                gameCharacter.setWisdom(Short.parseShort(message));
                break;
            case CHANGING_CHARACTER_CHA:
                gameCharacter.setCharisma(Short.parseShort(message));
                break;
            default:
                break;
        }
        return gameCharacter;
    }
}
