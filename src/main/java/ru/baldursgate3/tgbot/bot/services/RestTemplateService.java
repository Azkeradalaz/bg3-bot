package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.baldursgate3.tgbot.bot.Constant;
import ru.baldursgate3.tgbot.bot.entities.User;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestTemplateService {
    private final RestTemplate restTemplate;

    public String getUser(Long tgId) {
        String url = Constant.HOST + "/user";
        Map<String, String> request = new HashMap<>();
        request.put("tgUserId", tgId.toString());
        return restTemplate.getForObject(url, String.class, request);
    }

    public String getUserByTgId(Long tgId) {
        String url = Constant.HOST + "/user/tgid/" + tgId;
        return restTemplate.getForObject(url, String.class);
    }

    public String registerUser(Long id, String name) {
        String url = Constant.HOST + "/user";
        User user = new User();
        user.setTgUserId(id);
        user.setName(name);
        return restTemplate.postForObject(url, user, String.class);
    }
}
