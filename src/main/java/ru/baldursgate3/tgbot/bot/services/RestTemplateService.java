package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.baldursgate3.tgbot.bot.entities.GameCharacter;
import ru.baldursgate3.tgbot.bot.entities.User;

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

    public User getUserByTgId(Long tgId) {
        String url = HOST + "/user/tgid/" + tgId;
        return restTemplate.getForObject(url, User.class);
    }

    public String registerUser(Long id, String name) {
        String url = HOST + "/user";
        User user = new User();
        user.setTgUserId(id);
        user.setName(name);
        return restTemplate.postForObject(url, user, String.class);
    }

    public String saveGameCharacter(GameCharacter gameCharacter) {
        String url = HOST + "/character";
        return "Персонаж " + restTemplate.postForObject(url, gameCharacter, GameCharacter.class).getName() + " сохранён";
    }

    public List<GameCharacter> getListOfGameCharacters(Long userId){
        String url = HOST + "/character/"+userId;
        List<GameCharacter> gameCharacterList = restTemplate.getForObject(url, List.class);
        System.out.println(gameCharacterList);
        return gameCharacterList;
    }

}
