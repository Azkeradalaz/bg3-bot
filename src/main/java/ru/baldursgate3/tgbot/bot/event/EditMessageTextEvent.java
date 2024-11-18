package ru.baldursgate3.tgbot.bot.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Getter
public class EditMessageTextEvent extends ApplicationEvent {

    private EditMessageText editMessageText;

    public EditMessageTextEvent(Object source, EditMessageText editMessageText) {
        super(source);
        this.editMessageText = editMessageText;
    }
}
