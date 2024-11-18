package ru.baldursgate3.tgbot.bot.event;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import java.util.Arrays;
import java.util.List;

@Getter
public class DeleteMessagesEvent extends ApplicationEvent {

    private List<DeleteMessage> deleteMessages;

    public DeleteMessagesEvent(Object source, List<DeleteMessage> deleteMessages) {
        super(source);
        this.deleteMessages = deleteMessages;
    }
}
