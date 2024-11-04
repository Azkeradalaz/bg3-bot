package ru.baldursgate3.tgbot.bot.services;

import org.springframework.stereotype.Service;
import ru.baldursgate3.tgbot.bot.enums.UserState;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserStateService {
    private final Map<Long, UserState> userStateMap = new HashMap<>();

    public boolean isOfState(Long userId, UserState userState){
        return userStateMap.get(userId) == userState;
    }
    public void set(Long userId, UserState userState){
        userStateMap.put(userId, userState);
    }
    public UserState get(Long userId){
        return userStateMap.get(userId);
    }




}
