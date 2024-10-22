package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.baldursgate3.tgbot.bot.Constant;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestTemplateService {
    private final RestTemplate restTemplate;

    public String getUser(Long tgId) {
        String url = Constant.HOST +"/user";
        Map<String,String> request = new HashMap<>();
        request.put("tgUserId", tgId.toString());
        return restTemplate.getForObject(url, String.class, request);
    }
}
