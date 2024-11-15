package ru.baldursgate3.tgbot.bot.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Getter
public class SendMessageEvent extends ApplicationEvent {

    private SendMessage sendMessage;
    public SendMessageEvent(Object source, SendMessage sendMessage) {
        super(source);
        this.sendMessage = sendMessage;
    }
}
