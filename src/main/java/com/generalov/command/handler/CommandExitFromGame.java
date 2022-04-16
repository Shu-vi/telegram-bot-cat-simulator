package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandExitFromGame extends Command{
    public CommandExitFromGame(CatBot catBot){
        super(catBot);
    }

    private void exitFromGame(Long userId){
        database.setUserConditionByUserId(User.NOT_IN_GAME, userId);
        Integer catId = database.getCatByUserIdAndCatStatus(userId, true).getId();
        database.setCatOnlineStatus(false, catId);
        congratulationsMessage(userId);
    }

    public void exitFromGame(Update update){
        Long userId = update.getMessage().getChatId();
        Short userCondition = database.getUserById(userId).getCondition();
        if (userCondition == User.IN_GAME){
            exitFromGame(userId);
        } else {
            wrongConditionMessage(userId);
        }
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
