package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.entity.User;
import com.generalov.string.handler.StringHandler;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandEnterOn extends Command{
    public CommandEnterOn(CatBot catBot) {
        super(catBot);
    }

    private void enterOn(String message, Long userId){
        //Изменить статус у кота и у игрока
        String catName = getCatName(message);
        Integer catId = database.getCatByCatNameAndUserId(catName, userId).getId();
        if (isCatExist(catId)){
            database.setUserConditionByUserId(User.IN_GAME, userId);
            database.setCatOnlineStatus(true, catId);
            congratulationMessage(userId);
            //вывести информацию о локации
        } else {
            catNotExistMessage(userId);
        }
    }

    public void enterOn(Update update){
        Long userId = update.getMessage().getChatId();
        String message = StringHandler.deleteBotName(update.getMessage().getText());
        Short userCondition = database.getUserById(userId).getCondition();
        if (userCondition == User.NOT_IN_GAME) {
            enterOn(message, userId);
        } else {
            alreadyInGameMessage(userId);
        }
    }

    private String getCatName(String message){
        return message.substring(9);
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

    private Boolean isCatExist(Integer catId){
        return catId!=null;
    }

    @SneakyThrows
    private void catNotExistMessage(Long userId){
        String message = "У вас нет кота с таким именем. Попробуйте ввести \"Мои коты\", чтобы посмотреть список ваших котов.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }
}
