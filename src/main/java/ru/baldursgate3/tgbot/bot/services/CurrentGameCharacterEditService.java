package ru.baldursgate3.tgbot.bot.services;

import org.springframework.stereotype.Service;
import ru.baldursgate3.tgbot.bot.model.GameCharacterDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class CurrentGameCharacterEditService {
    private final Map<Long, GameCharacterDto> activeGameCharacter = new HashMap<>();

    public void put(Long userId, GameCharacterDto gameCharacterDto) {
        activeGameCharacter.put(userId, gameCharacterDto);
    }

    public GameCharacterDto get(Long userId){
        return activeGameCharacter.get(userId);
    }
    public void remove(Long userId){
        activeGameCharacter.remove(userId);
    }
}
