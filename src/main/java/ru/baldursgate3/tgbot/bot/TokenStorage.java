package ru.baldursgate3.tgbot.bot;

import java.io.IOException;

public final class TokenStorage {
    private static String token;

    {
        try {
            token = new String((TokenStorage.class.getResourceAsStream("/resources/api-token.txt")).readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getToken(){
        return token;
    }
}
