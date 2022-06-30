package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.dao.cat.CatDao;
import com.generalov.database.dao.user.UserDao;
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
    private CatDao catDao;
    private UserDao userDao;

    @Autowired
    public CommandExitFromGame(CatBot catBot, CatDao catDao, UserDao userDao) {
        super(catBot);
        this.catDao = catDao;
        this.userDao = userDao;
    }

    @Override
    public void useCommand(Update update) {
        Long userId = update.getMessage().getChatId();
        Short userCondition = userDao.read(userId).getCondition();
        if (userCondition == User.IN_GAME){
            exitFromGame(userId);
            congratulationsMessage(userId);
        } else {
            wrongConditionMessage(userId);
        }
    }

    private void exitFromGame(Long userId){
        Cat cat = catDao.readCatByUserId(userId);
        cat.setIsOnline(false);
        catDao.update(cat);
        User user = userDao.read(userId);
        user.setCondition(User.NOT_IN_GAME);
        userDao.update(user);
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
