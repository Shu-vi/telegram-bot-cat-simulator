package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.dao.user.UserDao;
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
    private UserDao userDao;

    @Autowired
    public CommandLocations(CatBot catBot, UserDao userDao) {
        super(catBot);
        this.userDao = userDao;
    }

    @Override
    public void useCommand(Update update) {
        Long userId = update.getMessage().getChatId();
        Short userCondition = userDao.read(userId).getCondition();
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
