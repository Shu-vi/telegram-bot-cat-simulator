package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.Database;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Scope(value = "singleton")
public class CommandExitFromGame extends Command{
    @Autowired
    public CommandExitFromGame(CatBot catBot, Database database) {
        super(catBot, database);
    }

    @Override
    public void useCommand(Update update) {
        Long userId = update.getMessage().getChatId();
        Short userCondition = database.getUserById(userId).getCondition();
        if (userCondition == User.IN_GAME){
            exitFromGame(userId);
            congratulationsMessage(userId);
        } else {
            wrongConditionMessage(userId);
        }
    }

    private void exitFromGame(Long userId){
        Cat cat = database.getCatByUserIdAndCatStatus(userId, true);
        cat.setIsOnline(false);
        database.setCat(cat);
        database.setUserConditionByUserId(User.NOT_IN_GAME, userId);
    }

    @SneakyThrows
    private void congratulationsMessage(Long userId){
        String message = "Вы вышли из игры.";
        catBot.execute(SendMessage.builder().text(message).chatId(userId.toString()).build());
    }

    @SneakyThrows
    private void wrongConditionMessage(Long userId){
        String message = "Выйдите из укрытия и дождитесь завершения всех действий.";
        catBot.execute(SendMessage.builder().text(message).chatId(userId.toString()).build());
    }
}
