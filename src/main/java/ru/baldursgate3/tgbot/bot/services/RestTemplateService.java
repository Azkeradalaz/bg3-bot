package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.baldursgate3.tgbot.bot.model.GameCharacterDto;
import ru.baldursgate3.tgbot.bot.model.UserDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestTemplateService {
    private final RestTemplate restTemplate;
    @Value("${host}")
    private String HOST;

    public String getUser(Long tgId) {
        String url = HOST + "/user";
        Map<String, String> request = new HashMap<>();
        request.put("tgUserId", tgId.toString());
        return restTemplate.getForObject(url, String.class, request);
    }

    public UserDto getUserByTgId(Long tgId) {
        String url = HOST + "/user/tgid/" + tgId;
        return restTemplate.getForObject(url, UserDto.class);
    }

    public String registerUser(Long id, String name) {
        String url = HOST + "/user";
        UserDto user = new UserDto(name,id);
        return restTemplate.postForObject(url, user, String.class);
    }

    public String saveGameCharacter(GameCharacterDto gameCharacter) {

        if(gameCharacter.id()==null){
            String url = HOST + "/character";
            restTemplate.postForObject(url, gameCharacter, String.class);
        }
        else {
            String url = HOST + "/character/"+gameCharacter.id();
            restTemplate.put(url,gameCharacter);
        }
        return "Персонаж " + gameCharacter.name() + " сохранён";
    }

    public List<GameCharacterDto> getListOfGameCharacters(Long userTgId){
        String url = HOST + "/character/tgid/"+userTgId;
        List<GameCharacterDto> gameCharacterList = restTemplate.getForObject(url, List.class);
        return gameCharacterList;
    }

    public GameCharacterDto getGameCharacter(Long gameCharId){
        String url = HOST + "/character/"+gameCharId;
        GameCharacterDto gameCharacterDto =restTemplate.getForObject(url,GameCharacterDto.class);
        return gameCharacterDto;
    }

    public void deleteCharacter(Long charId){
        String url = HOST + "/character/"+charId;
        restTemplate.delete(url);
    }

}
