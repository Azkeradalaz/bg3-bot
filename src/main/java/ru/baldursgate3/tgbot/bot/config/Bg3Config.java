package ru.baldursgate3.tgbot.bot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@Getter
@Setter
@ConfigurationProperties(prefix = "bg3")
public class Bg3Config {

    private String host;
    protected String token;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
