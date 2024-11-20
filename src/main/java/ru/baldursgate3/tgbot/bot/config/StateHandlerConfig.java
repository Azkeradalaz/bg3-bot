package ru.baldursgate3.tgbot.bot.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.baldursgate3.tgbot.bot.enums.UserState;
import ru.baldursgate3.tgbot.bot.states.*;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class StateHandlerConfig {
    private final MainMenuState mainMenuState;
    private final RegisterStateOne userRegisterStateOne;
    private final RegisterStateTwo userRegisterStateTwo;
    private final GameCharacterEditState gameCharacterEditState;
    private final GameCharacterListState gameCharacterListState;
    private final GameCharacterDeleteState gameCharacterDeleteState;

    @Bean
    public Map<UserState, SessionState> stateHandler(){
        return Map.ofEntries(
                Map.entry(UserState.MAIN_MENU, mainMenuState),
                Map.entry(UserState.REGISTER_STEP_ONE, userRegisterStateOne),
                Map.entry(UserState.REGISTER_STEP_TWO, userRegisterStateTwo),
                Map.entry(UserState.CHARACTER_EDIT, gameCharacterEditState),
                Map.entry(UserState.CHARACTER_LIST, gameCharacterListState),
                Map.entry(UserState.CHARACTER_DELETE, gameCharacterDeleteState)
        );
    }
}
