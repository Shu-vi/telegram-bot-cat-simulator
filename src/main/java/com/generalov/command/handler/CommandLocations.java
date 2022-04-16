package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.entity.User;
import com.generalov.properties.GetProperties;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandLocations extends Command{
    public CommandLocations(CatBot catBot){
        super(catBot);
    }

    @SneakyThrows
    private void locations(Long userId){
        String[] locations = GetProperties.getSpawnLocations();
        String text = "";
        for (int i = 0; i < locations.length; i++) {
            text += "-" + locations[i] + "\n";
        }
        catBot.execute(SendMessage.builder().text(text).chatId(userId.toString()).build());
    }

    public void locations(Update update){
        Long userId = update.getMessage().getChatId();
        Short userCondition = database.getUserById(userId).getCondition();
        if (userCondition == User.NOT_IN_GAME){
            locations(userId);
        } else {
            wrongConditionMessage(userId);
        }
    }

    @SneakyThrows
    private void wrongConditionMessage(Long userId){
        String text = "Для этой команды нужно выйти из игры. Пропишите команду \"Выйти из игры\" и введите команду \"Локации\" ещё раз.";
        catBot.execute(SendMessage.builder().text(text).chatId(userId.toString()).build());
    }
}
