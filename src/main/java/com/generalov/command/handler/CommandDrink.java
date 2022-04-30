package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.Location;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandDrink extends Command implements Runnable{
    private Update update;

    public CommandDrink(CatBot catBot, Update update) {
        super(catBot);
        this.update = update;
    }

    @SneakyThrows
    private void drink(){
        Long userId = update.getMessage().getChatId();
        Cat cat = database.getCatByUserIdAndCatStatus(userId, true);
        Location location = database.getLocationByLocationId(cat.getLocationId());
        Long waitingTimeMillis = Long.valueOf(calculateMillisOfDrinking(cat));
        if (isExistWater(location)){
            drinkingMessage(waitingTimeMillis);
            database.setUserConditionByUserId(User.DRINKING, userId);
            Thread.sleep(waitingTimeMillis);
            cat.setWater(100);
            database.setCat(cat);
            database.setUserConditionByUserId(User.IN_GAME, userId);
            congratulationMessage();
        } else {
            waterIsNotExistMessage();
        }
    }

    @Override
    public void run() {
        User user = database.getUserById(update.getMessage().getChatId());
        Short userCondition = user.getCondition();
        if (userCondition == User.IN_GAME) {
            drink();
        } else if (userCondition == User.NOT_IN_GAME) {
            notInGameMessage();
        } else {
            otherConditionsMessage();
        }
    }

    private Boolean isExistWater(Location location){
        return location.getWaterId() != null;
    }

    @SneakyThrows
    private void notInGameMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Вы сейчас не в игре. Зайдите в игру, чтобы попить.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());

    }

    @SneakyThrows
    private void drinkingMessage(Long waitingTimeMillis){
        Long userId = update.getMessage().getChatId();
        String message = "Вы пьёте. Действие завершится через " + waitingTimeMillis/1000 + " секунд";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void congratulationMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Действие завершено. Вы успешно попили.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void otherConditionsMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Выйдите из укрытия и завершите все действия..";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void waterIsNotExistMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "На этой локации нет воды.";
        catBot.execute(SendMessage.builder().text(message).chatId(userId.toString()).build());
    }

    private Integer calculateMillisOfDrinking(Cat cat){
        return (100 - cat.getWater()) * 6 * 1000;
    }
}
