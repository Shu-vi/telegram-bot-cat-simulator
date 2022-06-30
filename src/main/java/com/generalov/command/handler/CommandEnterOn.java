package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.dao.cat.CatDao;
import com.generalov.database.dao.user.UserDao;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.User;
import com.generalov.string.handler.StringHandler;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Scope(value = "singleton")
public class CommandEnterOn extends Command{
    private UserDao userDao;
    private CatDao catDao;

    @Autowired
    public CommandEnterOn(CatBot catBot, UserDao userDao, CatDao catDao) {
        super(catBot);
        this.userDao = userDao;
        this.catDao = catDao;
    }

    @Override
    public void useCommand(Update update) {
        Long userId = update.getMessage().getChatId();
        User user = userDao.read(userId);
        String message = StringHandler.deleteBotName(update.getMessage().getText());
        Short userCondition = user.getCondition();
        if (userCondition == User.NOT_IN_GAME) {
            enterOn(message, user);
        } else {
            alreadyInGameMessage(userId);
        }
    }

    private void enterOn(String message, User user){
        String catName = getCatName(message);
        Cat cat = catDao.readCatByCatName(catName);
        if (isCatExist(cat)){
            System.out.println(cat.getUserId() + " " + user.getId());
            if (cat.getUserId().equals(user.getId())){
                changeConditions(user, cat);
                congratulationMessage(user.getId());
            }
        } else {
            catNotExistMessage(user.getId());
        }
    }

    private String getCatName(String message){
        return message.substring(9);
    }

    private Boolean isCatExist(Cat cat){
        return cat!=null;
    }

    private void changeConditions(User user, Cat cat){
        user.setCondition(User.IN_GAME);
        cat.setIsOnline(true);
        userDao.update(user);
        catDao.update(cat);
    }

    @SneakyThrows
    private void catNotExistMessage(Long userId){
        String message = "У вас нет кота с таким именем. Попробуйте ввести \"Мои коты\", чтобы посмотреть список ваших котов.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void alreadyInGameMessage(Long userId){
        String text = "Вы уже в игре. Если хотите зайти на другого персонажа, для начала выйдите из игры.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(text).build());
    }

    @SneakyThrows
    private void congratulationMessage(Long userId){
        String text = "Вы успешно зашли в игру.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(text).build());
    }
}
