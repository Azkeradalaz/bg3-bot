package ru.baldursgate3.tgbot.bot.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;

@Getter
public class DeleteMessagesEvent extends ApplicationEvent {
    private DeleteMessages deleteMessages;

    public DeleteMessagesEvent(Object source, DeleteMessages deleteMessages) {
        super(source);
        this.deleteMessages = deleteMessages;
    }
}
