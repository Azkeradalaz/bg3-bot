package ru.baldursgate3.tgbot.bot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bg3")
@Getter
@Setter
public class Bg3ConfigProperties {
    private String host;
    protected String token;

}
