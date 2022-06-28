package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.Database;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
@Scope(value = "singleton")
public class CommandLocations extends Command{
    @Value("#{'${spawnLocations}'.split(',')}")
    private String[] locations;
    @Autowired
    public CommandLocations(CatBot catBot, Database database) {
        super(catBot, database);
    }


    @Override
    public void useCommand(Update update) {
        Long userId = update.getMessage().getChatId();
        Short userCondition = database.getUserById(userId).getCondition();
        if (userCondition == User.NOT_IN_GAME){
            locations(userId);
        } else {
            wrongConditionMessage(userId);
        }
    }

    @SneakyThrows
    private void locations(Long userId){
        String text = "";
        for (int i = 0; i < locations.length; i++) {
            text += getFormatLocationName(locations[i]);
        }
        catBot.execute(SendMessage.builder().text(text).chatId(userId.toString()).build());
    }

    private String getFormatLocationName(String location){
        return "-" + location + "\n";
    }
    @SneakyThrows
    private void wrongConditionMessage(Long userId){
        String text = "Для этой команды нужно выйти из игры. Пропишите команду \"Выйти из игры\" и введите команду \"Локации\" ещё раз.";
        catBot.execute(SendMessage.builder().text(text).chatId(userId.toString()).build());
    }
}
