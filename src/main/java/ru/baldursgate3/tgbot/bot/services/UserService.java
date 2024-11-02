package ru.baldursgate3.tgbot.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final RestTemplateService restTemplateService;
    private final MessageService messageService;
    private Set<Long> setOfUsersToRegister = new HashSet<>();

    public boolean isRegistered(Long userId) {
        return restTemplateService.getUserByTgId(userId) != null;
    }

    public void setToRegister(Long userId){
        setOfUsersToRegister.add(userId);
    }

    public boolean isSetToRegister(Long userId){
        return setOfUsersToRegister.contains(userId);
    }

    public String registerUser(Long userId, String name) {
        setOfUsersToRegister.remove(userId);
        return restTemplateService.registerUser(userId, name);
    }

    public String getUserName(Long userId) {
        return restTemplateService.getUserByTgId(userId).getName();
    }

    public SendMessage processNonRegisteredUser(Long chatId,Long userId,String messageText) {
        if (isSetToRegister(userId)){
            String registeredUser = registerUser(userId, messageText);
            return messageService.greetingRegisteredUser(chatId,registeredUser);
        }else {
            setToRegister(userId);
            return messageService.greetingNonRegisteredUser(chatId);
        }

    }
}
