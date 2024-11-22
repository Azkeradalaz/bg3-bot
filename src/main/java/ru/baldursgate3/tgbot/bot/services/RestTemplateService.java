package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.baldursgate3.tgbot.bot.model.GameCharacterDto;
import ru.baldursgate3.tgbot.bot.model.SessionDto;
import ru.baldursgate3.tgbot.bot.model.UserDto;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestTemplateService {
    private final RestTemplate restTemplate;
    @Value("${host}")
    private String HOST;

    /*---------------USER---------------*/

    public UserDto getUserByTgId(Long tgId) {
        String url = HOST + "/user/tgid/" + tgId;
        return restTemplate.getForObject(url, UserDto.class);
    }

    public void registerUser(Long id, String name) {
        String url = HOST + "/user/tgid/" + id;
        Optional<String> nameOpt = Optional.of(name);
        log.info("имя нового пользователя {}", nameOpt);
        restTemplate.put(url, nameOpt);
    }

    /*---------------GAME CHARACTER---------------*/
    public Long saveGameCharacter(GameCharacterDto gameCharacter) {
        Long id;
        if (gameCharacter.id() == null) {
            String url = HOST + "/character";
            id = restTemplate.postForObject(url, gameCharacter, Long.class);
        } else {
            id = gameCharacter.id();
            String url = HOST + "/character/" + gameCharacter.id();
            restTemplate.put(url, gameCharacter);
        }
        return id;
    }

    public List<GameCharacterDto> getListOfGameCharacters(Long userTgId) {
        String url = HOST + "/character/tgid/" + userTgId;
        List<GameCharacterDto> gameCharacterList = restTemplate.getForObject(url, List.class);
        return gameCharacterList;
    }

    public GameCharacterDto getGameCharacter(Long gameCharId) {
        String url = HOST + "/character/" + gameCharId;
        GameCharacterDto gameCharacterDto = restTemplate.getForObject(url, GameCharacterDto.class);
        return gameCharacterDto;
    }

    public void deleteGameCharacter(Long charId) {
        String url = HOST + "/character/" + charId;
        restTemplate.delete(url);
    }

    /*---------------SESSION---------------*/
    public SessionDto getSession(Long userId) {
        String url = HOST + "/session/" + userId;
        return restTemplate.getForObject(url, SessionDto.class);
    }

    public void updateSessionState(Long userId, String state) {
        String url = HOST + "/session/" + userId;
        restTemplate.put(url, state);
    }
    public void updateSessionGameCharacter(Long userId, Long gameCharacterId) {
        String url = HOST + "/session/gamechar/" + userId;
        Optional<Long> tmp = Optional.ofNullable(gameCharacterId);
        restTemplate.put(url, tmp);
    }
}
