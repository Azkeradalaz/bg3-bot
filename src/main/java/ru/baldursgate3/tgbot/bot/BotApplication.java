package ru.baldursgate3.tgbot.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.baldursgate3.tgbot.bot.config.Bg3ConfigProperties;

@SpringBootApplication
@EnableConfigurationProperties(Bg3ConfigProperties.class)
public class BotApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
